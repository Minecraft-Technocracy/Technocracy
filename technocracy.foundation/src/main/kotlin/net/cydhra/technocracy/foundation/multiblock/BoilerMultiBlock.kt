package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerHeater
import net.minecraft.block.BlockAir
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import java.util.function.Predicate
import kotlin.math.roundToInt

class BoilerMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerControllerBlock
        },
        sideBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerGlassBlock || it.block == boilerControllerBlock ||
                    it.block == boilerFluidInputBlock
        },
        topBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerGlassBlock || it.block == boilerFluidOutputBlock ||
                    it.block == boilerFluidInputBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerGlassBlock || it.block == boilerHeaterBlock ||
                    it.block == boilerFluidInputBlock
        },
        interiorBlockWhitelist = Predicate {
            it.block == Blocks.AIR || it.block == boilerConductorBlock
        },
        maximumSizeXZ = 16,
        maximumSizeY = 16,
        world = world) {

    /**
     * The controller tile entity of this multi block structure. Null until the block is found by [isMachineWhole]
     */
    var controllerTileEntity: TileEntityBoilerController? = null

    /**
     * The heaters of this structure. Empty until the machine is assembled successfully
     */
    var heaterElements: List<TileEntityBoilerHeater> = emptyList()

    /**
     * Steam that one heater produces per tick with the current setup
     */
    var steamPerHeaterPerTick: Int = 100

    /**
     * Energy consumed by one heater per tick
     */
    var energyPerHeaterPerTick: Int = 100

    override fun updateServer(): Boolean {
        controllerTileEntity?.doWork()
        return true
    }

    override fun updateClient() {

    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback))
            return false

        return assemble(validatorCallback) {
            val controllerTileEntities = mutableListOf<TileEntityBoilerController>()
            val heaterTileEntities = mutableListOf<TileEntityBoilerHeater>()

            collect(boilerControllerBlock.unlocalizedName, controllerTileEntities, 1)
            collect(heaterTileEntities)

            finishUp {
                this@BoilerMultiBlock.controllerTileEntity = controllerTileEntities.single()
                this@BoilerMultiBlock.heaterElements = heaterTileEntities

                return@finishUp this@BoilerMultiBlock.recalculateContents(validatorCallback)
            }
        }
    }

    /**
     * Recalculate how much water can be stored
     */
    private fun recalculateContents(validatorCallback: IMultiblockValidator): Boolean {
        val interiorMin = minimumCoord.add(1, 1, 1)
        val interiorMax = maximumCoord.add(-1, -1, -1)

        val height = interiorMax.y - interiorMin.y + 1
        val averageWidth = ((interiorMax.x - interiorMin.x + interiorMax.z - interiorMin.z) / 2.0).roundToInt() + 1

        if (height < averageWidth) {
            validatorCallback.setLastError("multiblock.error.wider_than_high", averageWidth, height)
            return false
        }

        var spaceInside = 0
        val heatQueue = ArrayDeque<BlockPos>()

        // a map of heat multipliers per block pos
        val heatMap = HashMap<BlockPos, Int>()

        // calculate fluid capacity (calculate bottom up, so conductors are placed in the right order)
        for (y in interiorMin.y..interiorMax.y) {
            for (x in interiorMin.x..interiorMax.x) {
                for (z in interiorMin.z..interiorMax.z) {
                    when (this.WORLD.getBlockState(BlockPos(x, y, z)).block) {
                        is BlockAir -> {
                            // add blocks to heat queue that are directly above heaters
                            if (WORLD.getBlockState(BlockPos(x, y - 1, z)).block == boilerHeaterBlock) {
                                heatQueue.addLast(BlockPos(x, y, z))
                            }
                            spaceInside++
                        }
                        boilerConductorBlock -> {
                            // add all conductors to heat map (bottom first)
                            heatQueue.addLast(BlockPos(x, y, z))
                        }
                    }
                }
            }
        }

        // update steam generation stats
        if (heaterElements.isEmpty()) {
            energyPerHeaterPerTick = 0
            steamPerHeaterPerTick = 0
        } else {
            // calculate heat map
            while (heatQueue.isNotEmpty()) {
                val pos = heatQueue.pop()
                val block = this.WORLD.getBlockState(BlockPos(pos.x, pos.y, pos.z)).block
                if (block == boilerConductorBlock) {
                    heatMap[pos] = when {
                        heatMap[pos.down()] == MultiBlockPhysics.conductorTemperature -> MultiBlockPhysics.conductorTemperature
                        WORLD.getBlockState(pos.down()).block == boilerHeaterBlock -> MultiBlockPhysics.conductorTemperature
                        else -> 0
                    }

                    // update neighbors
                    arrayOf(pos.north(), pos.east(), pos.south(), pos.west(), pos.up())
                            .filter { this.WORLD.getBlockState(BlockPos(it)).block is BlockAir }
                            .forEach(heatQueue::addLast)
                } else if (block is BlockAir) { // do not calculate wall blocks and outside
                    // calculate maximal heat of all neighbor blocks
                    val neighborHeat =
                            if (WORLD.getBlockState(pos.down()).block == boilerHeaterBlock)
                                MultiBlockPhysics.conductorTemperature
                            else
                                arrayOf(pos.down(), pos.north(), pos.east(), pos.south(), pos.west())
                                        .mapNotNull { heatMap[it] }
                                        .max() ?: 0

                    // if current heat is lower than neighbor heat and falloff, update it
                    if (heatMap[pos] ?: 0 < neighborHeat - 1) {
                        heatMap[pos] = neighborHeat - 1
                        // update neighbors
                        arrayOf(pos.north(), pos.east(), pos.south(), pos.west(), pos.up())
                                .filter { this.WORLD.getBlockState(BlockPos(it)).block is BlockAir }
                                .forEach(heatQueue::addLast)
                    }
                }
            }

            var totalEnergyUsage = 0
            var totalSteamProduction = 0

            heatMap.values
                    .filter { heat -> heat in 1..MultiBlockPhysics.conductorTemperature }
                    .forEach { heat ->
                        // increase energy cost for every heated block
                        totalEnergyUsage += MultiBlockPhysics.baseEnergyUsage

                        // increase steam production for every heated block that is not a conductor
                        if (heat < MultiBlockPhysics.conductorTemperature)
                            totalSteamProduction += MultiBlockPhysics.baseSteamGeneration * heat
                    }

            this.steamPerHeaterPerTick = totalSteamProduction / heaterElements.size
            this.energyPerHeaterPerTick = totalEnergyUsage / heaterElements.size

        }

        // update fluid capacity
        this.controllerTileEntity!!.updateCapacity(spaceInside * 4000)

        return true
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {

    }

    override fun onBlockRemoved(p0: IMultiblockPart?) {

    }

    override fun onAssimilate(p0: MultiblockControllerBase?) {

    }

    override fun onAttachedPartWithMultiblockData(p0: IMultiblockPart?, p1: NBTTagCompound?) {

    }

    override fun onMachineAssembled() {

    }

    override fun syncDataFrom(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {

    }

    override fun onAssimilated(p0: MultiblockControllerBase?) {

    }

    override fun onMachineRestored() {

    }

    override fun syncDataTo(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {

    }

    override fun onMachinePaused() {

    }

    override fun onMachineDisassembled() {

    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 26
    }

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        val components = mutableListOf<Pair<String, AbstractComponent>>()
        heaterElements.forEach { components.addAll(it.getComponents()) }
        if (controllerTileEntity != null) components.addAll(controllerTileEntity!!.getComponents())
        return components
    }

}