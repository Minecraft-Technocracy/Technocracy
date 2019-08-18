package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorController
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorEnergyPort
import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CapacitorMultiBlock(val world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate { it.block == capacitorWallBlock },
        sideBlockWhitelist = Predicate {
            it.block == capacitorWallBlock || it.block == capacitorControllerBlock
        },
        topBlockWhitelist = Predicate {
            it.block == capacitorConnectorBlock || it.block == capacitorWallBlock || it.block ==
                    capacitorEnergyPortBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == capacitorWallBlock
        },
        interiorBlockWhitelist = Predicate {
            it.block == sulfuricAcidBlock || it.block == capacitorElectrodeBlock
        },
        maximumSizeXZ = 20,
        maximumSizeY = 20,
        world = world
) {

    var controllerTileEntity: TileEntityCapacitorController? = null

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        val components = mutableListOf<Pair<String, AbstractComponent>>()
        if (controllerTileEntity != null)
            components.addAll(controllerTileEntity!!.getComponents())
        return components
    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback)) return false

        return assemble(validatorCallback) {
            val controllers = mutableListOf<TileEntityCapacitorController>()
            val energyPorts = mutableListOf<TileEntityCapacitorEnergyPort>()

            collect(capacitorControllerBlock.unlocalizedName, controllers, 1)
            val maxPorts = (maximumXSize - 4) * (maximumYSize - 4) / 2
            collect(capacitorEnergyPortBlock.unlocalizedName, energyPorts, 1..maxPorts)

            finishUp {
                this@CapacitorMultiBlock.controllerTileEntity = controllers.first()

                return@finishUp this@CapacitorMultiBlock.recalculatePhysics(validatorCallback)
            }
        }
    }

    private fun recalculatePhysics(validatorCallback: IMultiblockValidator): Boolean {
        val interiorMin = minimumCoord.add(1, 1, 1)
        val interiorMax = maximumCoord.add(-1, -1, -1)

        //Find all electrodes
        val electrodes = mutableSetOf<Electrode>()
        for (x in interiorMin.x..interiorMax.x) {
            for (z in interiorMin.z..interiorMax.z) {
                for (y in interiorMin.y..interiorMax.y) {
                    val block = this.world.getBlockState(BlockPos(x, y, z)).block
                    if (block == capacitorElectrodeBlock)
                        electrodes += Electrode(x, z, 0, block)
                }
            }
        }

        val electrodeParts = mutableListOf<Triple<Int, Int, Int>>()
        //Check electrodes
        electrodes.forEach {
            for (y in (interiorMin.y)..interiorMax.y) {
                val block = this.world.getBlockState(BlockPos(it.x, y, it.z)).block
                //Checks side blocks, then the bottom most block
                if ((this.world.getBlockState(BlockPos(it.x + 1, y, it.z)).block != sulfuricAcidBlock ||
                                this.world.getBlockState(
                                        BlockPos(it.x, y, it.z + 1)).block != sulfuricAcidBlock ||
                                this.world.getBlockState(
                                        BlockPos(it.x - 1, y, it.z)).block != sulfuricAcidBlock ||
                                this.world.getBlockState(
                                        BlockPos(it.x, y, it.z - 1)).block != sulfuricAcidBlock) ||
                        (y == interiorMin.y && block != sulfuricAcidBlock)) {
                    validatorCallback.setLastError("multiblock.error.invalid_electrode_placement", it.x, y,
                            it.z)
                    return false
                } else if (it.height != 0 && y != interiorMin.y && block != it.block) {
                    validatorCallback.setLastError("multiblock.error.electrode_not_connected", it.x, y,
                            it.z)
                    return false
                } else if(block == it.block) {
                    electrodeParts += Triple(it.x, y, it.z)
                    it.height++
                }
            }
        }

        if (electrodes.size % 2 != 0) {
            validatorCallback.setLastError("multiblock.error.uneven_number_of_electrodes")
            return false
        }

        //Calculates the average shortest distance between electrode parts
        var totalDistance = 0
        electrodeParts.forEach {
            //Definitely longer than the longest possible distance
            var shortestDistance = maximumXSize * maximumZSize
            electrodeParts.forEach { other ->
                if (other != it) {
                    val distance = (it.first - other.first) * (it.first - other.first) +
                            (it.second - other.second) * (it.second - other.second) +
                            (it.third - other.third) * (it.third - other.third)
                    if (distance < shortestDistance)
                        shortestDistance = distance
                }
            }
            totalDistance += shortestDistance
        }

        //capacity = distance * 512000 * number_of_internal_blocks / number_of_electrodes
        controllerTileEntity!!.energyStorageComponent.energyStorage.capacity = (sqrt(totalDistance.toDouble()) *
                512000 *
                ((interiorMax.x - interiorMin.x) * (interiorMax.y - interiorMin.y) * (interiorMax.z - interiorMin.z))
                / electrodes.size).roundToInt()

        //drain/fill speed = 2000 * number_of_electrodes
        controllerTileEntity!!.energyStorageComponent.energyStorage.extractionLimit = 2000 * electrodes.size
        controllerTileEntity!!.energyStorageComponent.energyStorage.receivingLimit = 2000 * electrodes.size

        return true
    }

    override fun updateServer(): Boolean {
        return false
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 110
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {
    }

    override fun updateClient() {
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
}

private class Electrode(val x: Int, val z: Int, var height: Int, val block: Block) {
    override fun equals(other: Any?): Boolean = other is Electrode && other.x == x && other.z == z
    override fun hashCode(): Int = 31 * x + z
}