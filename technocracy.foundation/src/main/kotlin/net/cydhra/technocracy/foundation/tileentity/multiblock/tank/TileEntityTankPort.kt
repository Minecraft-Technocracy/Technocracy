package net.cydhra.technocracy.foundation.tileentity.multiblock.tank

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


class TileEntityTankPort : TileEntityTankMultiBlockPart() {
    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled)
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (hasCapability(capability, facing)) {
            if (multiblockController != null && multiblockController?.controllerTileEntity != null) {
                val fluidComp = multiblockController?.controllerTileEntity!!.fluidComp
                if (fluidComp.isAttached) {
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast<T>(
                            multiblockController!!.controllerTileEntity!!.fluidComp.innerComponent.fluid)
                }
            }
        }
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast<T>(DynamicFluidHandler())
    }
}