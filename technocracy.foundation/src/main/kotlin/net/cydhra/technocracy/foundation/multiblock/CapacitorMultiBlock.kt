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
import net.minecraft.util.EnumFacing
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
            it.block == sulfuricAcidBlock || it.block == capacitorElectrodeBlock || it.block ==
                    capacitorOxidizedElectrodeBlock
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
        val interiorVolume = (interiorMax.x - interiorMin.x) * (interiorMax.y - interiorMin.y) *
                (interiorMax.z - interiorMin.z)

        //Find all electrodes
        val electrodes = mutableSetOf<Electrode>()
        for (x in interiorMin.x..interiorMax.x) {
            for (z in interiorMin.z..interiorMax.z) {
                for (y in interiorMin.y..interiorMax.y) {
                    val block = this.world.getBlockState(BlockPos(x, y, z)).block
                    if (block == capacitorElectrodeBlock || block == capacitorOxidizedElectrodeBlock)
                        electrodes += Electrode(x, z, block)
                }
            }
        }

        val electrodeBlocks = mutableListOf<Triple<Int, Int, Int>>()
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
                    validatorCallback.setLastError("multiblock.error.invalid_electrode_placement", it.x, y, it.z)
                    return false
                } else if (it.height != 0 && y != interiorMin.y && block != it.block) {
                    validatorCallback.setLastError("multiblock.error.electrode_not_connected", it.x, y, it.z)
                    return false
                } else if (block == it.block) {
                    electrodeBlocks += Triple(it.x, y, it.z)
                    it.height++
                }
            }
        }

        //Check rows and columns for adjacent electrodes of the same type
        //Finds all electrodes that are in each row and column, sorts them and then checks if any of the same type are
        // adjacent
        for (col in interiorMin.x + 1 until interiorMax.x) {
            var previousBlock: Block? = null
            electrodes.filter { it.x == col }.sortedBy { it.x }.forEach {
                if (previousBlock != null && previousBlock == it.block) {
                    validatorCallback.setLastError("multiblock.error.adjacent_electrode_types", it.x, interiorMax.y,
                            it.z)
                    return false
                }
                previousBlock = it.block
            }
        }

        for (row in interiorMin.z + 1 until interiorMax.z) {
            var previousBlock: Block? = null
            electrodes.filter { it.z == row }.sortedBy { it.z }.forEach {
                if (previousBlock != null && previousBlock == it.block) {
                    validatorCallback.setLastError("multiblock.error.adjacent_electrode_types", it.x, interiorMax.y,
                            it.z)
                    return false
                }
                previousBlock = it.block
            }
        }

        val connections = mutableListOf<Pair<Electrode, Electrode>>()
        //Check connections
        electrodes.forEach {
            if (connections.none { connection -> connection.first == it || connection.second == it }) {
                //The current pos in the connection
                var pos = BlockPos(it.x, interiorMax.y + 1, it.z)
                var lastFacing = EnumFacing.UP
                while (true) {
                    //First electrode is always `it` and the second electrode will be defined by `pos` and `block` if one is found

                    //Whether there is an electrode at the current pos
                    val isElectrodePos = electrodes.any { e -> e.x == pos.x && e.z == pos.z }
                    val block = this.world.getBlockState(pos).block
                    if (isElectrodePos && block != capacitorConnectorBlock && block != capacitorEnergyPortBlock) {
                        validatorCallback.setLastError("multiblock.error.invalid_block_above_electrode", pos.x, pos.y,
                                pos.z)
                        return false
                    }

                    var newFacing: EnumFacing? = null
                    var connectedBlocks = 0
                    //Loop through all three possible directions
                    for (direction in EnumFacing.HORIZONTALS) {
                        if (direction == lastFacing) {
                            //To properly count all connected blocks
                            connectedBlocks++
                            continue
                        }

                        //Check if block in current direction is a valid connector block
                        val currentBlock: Block = this.world.getBlockState(pos.offset(direction)).block
                        if (currentBlock == capacitorConnectorBlock || currentBlock == capacitorEnergyPortBlock) {
                            connectedBlocks++
                            newFacing = direction
                        }
                    }


                    //More than two connections, or if the block below is an electrode only one connection is allowed
                    if (connectedBlocks > 2 || (connectedBlocks > 1 && isElectrodePos)) {
                        validatorCallback.setLastError("multiblock.error.too_many_connections", pos.x, pos.y, pos.z)
                        return false
                    }

                    if (isElectrodePos && !(pos.x == it.x && pos.z == it.z)) {
                        if (block == it.block) {
                            validatorCallback.setLastError("multiblock.error.connection_of_same_electrode_types", pos.x,
                                    pos.y, pos.z)
                            return false
                        }

                        //Found end, create new connection and return
                        val connection = Pair(it, Electrode(pos.x, pos.z, block))
                        connections.add(connection)
                        break
                    }

                    if (newFacing == null) {
                        validatorCallback.setLastError("multiblock.error.uncompleted_connection", pos.x, pos.y, pos.z)
                        return false
                    }

                    lastFacing = newFacing.opposite
                    pos = pos.offset(newFacing)
                }
            }
        }

        if (electrodes.size % 2 != 0 || electrodes.filter { electrode -> electrode.block == capacitorElectrodeBlock }.size != electrodes.size / 2) {
            validatorCallback.setLastError("multiblock.error.uneven_number_of_electrodes")
            return false
        }

        //https://github.com/Cydhra/Technocracy/issues/28#issuecomment-522795108 and following

        //Calculates the average shortest distance from each interior block to the next electrode block
        var totalDistance = 0
        for (x in interiorMin.x..interiorMax.x) {
            for (z in interiorMin.z..interiorMax.z) {
                for (y in interiorMin.y..interiorMax.y) {
                    val blockState = this.world.getBlockState(BlockPos(x, y, z))
                    if (blockState.block != sulfuricAcidBlock)
                        continue

                    //Definitely longer than the longest possible distance
                    var shortestDistance = maximumXSize * maximumZSize
                    electrodeBlocks.forEach { other ->
                        if (!(other.first == x && other.second == y && other.third != z)) {
                            //Calculate distance (x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2
                            val distance = (x - other.first) * (x - other.first) +
                                    (y - other.second) * (y - other.second) +
                                    (z - other.third) * (z - other.third)
                            if (distance < shortestDistance)
                                shortestDistance = distance
                        }
                    }
                    totalDistance += shortestDistance
                }
            }
        }

        //Divide to get average
        totalDistance /= interiorVolume

        //capacity = averageDistanceSq * 512000 * number_of_internal_blocks / sqrt(2 * number_of_electrodes)
        controllerTileEntity!!.energyStorageComponent.energyStorage.capacity = (sqrt(totalDistance.toDouble()) *
                512000 *
                interiorVolume
                / sqrt(2.0 * electrodeBlocks.size)).roundToInt()

        //drain/fill speed = 200 * number_of_electrodes^2
        val transferSpeed = 200 * electrodeBlocks.size * electrodeBlocks.size
        controllerTileEntity!!.energyStorageComponent.energyStorage.extractionLimit = transferSpeed
        controllerTileEntity!!.energyStorageComponent.energyStorage.receivingLimit = transferSpeed

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

private class Electrode(val x: Int, val z: Int, val block: Block) {
    var height: Int = 0

    override fun equals(other: Any?): Boolean = other is Electrode && other.x == x && other.z == z
    override fun hashCode(): Int = 31 * x + z
}