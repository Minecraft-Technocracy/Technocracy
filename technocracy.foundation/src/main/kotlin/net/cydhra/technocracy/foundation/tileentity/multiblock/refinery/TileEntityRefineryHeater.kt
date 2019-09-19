package net.cydhra.technocracy.foundation.tileentity.multiblock.refinery

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage
import net.cydhra.technocracy.foundation.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class TileEntityRefineryHeater : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock) {

    val energyStorageComponent: EnergyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.DOWN))

    init {
        this.registerComponent(energyStorageComponent, "energy")
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled) {
            this.supportsCapability(capability, facing) || super.hasCapability(capability, facing)
        } else false
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (this.hasCapability(capability, facing))
            super.getCapability(capability, facing)
        else
            null
    }
}