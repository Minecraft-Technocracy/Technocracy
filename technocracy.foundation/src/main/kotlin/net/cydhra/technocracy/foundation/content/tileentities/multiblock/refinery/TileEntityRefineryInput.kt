package net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery

import net.cydhra.technocracy.foundation.content.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityRefineryInput : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock) {

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null)
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(multiblockController?.controllerTileEntity?.inputComponent?.fluid)
        else
            null
    }
}