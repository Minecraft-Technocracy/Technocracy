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
import net.minecraft.world.World
import java.util.function.Predicate

class CapacitorMultiBlock(world: World) : BaseMultiBlock(
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

    private var energyPorts: List<TileEntityCapacitorEnergyPort> = emptyList()

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        val components = mutableListOf<Pair<String, AbstractComponent>>()
        if(controllerTileEntity != null)
            components.addAll(controllerTileEntity!!.getComponents())
        energyPorts.forEach { components.addAll(it.getComponents()) }
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
                this@CapacitorMultiBlock.energyPorts = energyPorts

                return@finishUp this@CapacitorMultiBlock.recalculatePhysics(validatorCallback)
            }
        }
    }

    private fun recalculatePhysics(validatorCallback: IMultiblockValidator): Boolean {
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