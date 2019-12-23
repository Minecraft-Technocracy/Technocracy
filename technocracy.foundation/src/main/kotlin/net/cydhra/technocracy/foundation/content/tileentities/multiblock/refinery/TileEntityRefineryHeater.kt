package net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery

import net.cydhra.technocracy.foundation.content.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class TileEntityRefineryHeater : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock) {

    val energyStorageComponent: EnergyStorageTileEntityComponent = EnergyStorageTileEntityComponent(mutableSetOf(EnumFacing.DOWN))

    init {
        this.registerComponent(energyStorageComponent, "energy")
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null) {
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