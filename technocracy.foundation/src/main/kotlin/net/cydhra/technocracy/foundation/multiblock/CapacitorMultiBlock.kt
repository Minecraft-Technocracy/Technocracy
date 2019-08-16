package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorController
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorEnergyPort
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

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
        val electrodePositions = mutableListOf<Pair<Int, Int>>()
        for (x in interiorMin.x until interiorMax.x) {
            for (z in interiorMin.z until interiorMax.z) {
                val block = this.world.getBlockState(BlockPos(x, interiorMin.y + 1, z)).block
                if (block == capacitorElectrodeBlock) {
                    if(this.world.getBlockState(BlockPos(x, interiorMin.y, z)).block != sulfuricAcidBlock) {
                        validatorCallback.setLastError("multiblock.error.invalid_electrode_placement", x, interiorMin.y, z)
                        return false
                    }
                    electrodePositions += Pair(x, z)
                }
            }
        }

        //Check surroundings of electrodes
        electrodePositions.forEach {
            for (y in (interiorMin.y + 1)..interiorMax.y) {
                if (this.world.getBlockState(BlockPos(it.first + 1, y, it.second)).block != sulfuricAcidBlock ||
                        this.world.getBlockState(BlockPos(it.first, y, it.second + 1)).block != sulfuricAcidBlock ||
                        this.world.getBlockState(BlockPos(it.first - 1, y, it.second)).block != sulfuricAcidBlock ||
                        this.world.getBlockState(BlockPos(it.first, y, it.second - 1)).block != sulfuricAcidBlock) {
                    validatorCallback.setLastError("multiblock.error.invalid_electrode_placement", it.first, y,
                            it.second)
                    return false
                }
            }
        }

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