package net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


class TileEntityTankPort : TileEntityTankMultiBlockPart() {
    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null)
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (hasCapability(capability, facing)) {
            if (multiblockController != null && multiblockController?.controllerTileEntity != null) {
                val fluidComp = multiblockController?.controllerTileEntity!!.fluidComp
                if (fluidComp.isAttached) {
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast<T>(
                        fluidComp.innerComponent.fluid
                    )
                }
            }
        }
        return null
    }
}