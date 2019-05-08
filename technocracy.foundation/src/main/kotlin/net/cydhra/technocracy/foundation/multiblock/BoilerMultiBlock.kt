package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerHeater
import net.minecraft.block.BlockAir
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

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
            it.block == Blocks.AIR
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

            onSuccess {
                this@BoilerMultiBlock.controllerTileEntity = controllerTileEntities.single()
                this@BoilerMultiBlock.heaterElements = heaterTileEntities

                this@BoilerMultiBlock.recalculateContents()
                this@BoilerMultiBlock.recalculateStats()
            }
        }
    }

    /**
     * Recalculate how much water can be stored
     */
    private fun recalculateContents() {
        val interiorMin = minimumCoord.add(1, 1, 1)
        val interiorMax = maximumCoord.add(-1, -1, -1)

        var spaceInside = 0

        // calculate fluid capacity
        for (x in interiorMin.x..interiorMax.x) {
            for (y in interiorMin.y..interiorMax.y) {
                for (z in interiorMin.z..interiorMax.z) {
                    if (this.WORLD.getBlockState(BlockPos(x, y, z)).block is BlockAir)
                        spaceInside++
                }
            }
        }

        this.controllerTileEntity!!.updateCapacity(spaceInside * 4000)
    }

    /**
     * Recalculate stats like energy and steam per tick
     */
    private fun recalculateStats() {
        // TODO
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
}