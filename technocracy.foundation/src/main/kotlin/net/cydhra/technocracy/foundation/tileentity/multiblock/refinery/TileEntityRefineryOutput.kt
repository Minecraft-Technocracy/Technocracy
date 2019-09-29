package net.cydhra.technocracy.foundation.tileentity.multiblock.refinery

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityRefineryOutput : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock) {

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null)
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.multiblockController?.getOutputTank(this))
        else
            null
    }
}