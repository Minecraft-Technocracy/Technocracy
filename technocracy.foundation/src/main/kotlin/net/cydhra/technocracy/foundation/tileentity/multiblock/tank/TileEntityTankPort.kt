package net.cydhra.technocracy.foundation.tileentity.multiblock.tank

import net.cydhra.technocracy.foundation.multiblock.TankMultiBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


class TileEntityTankPort : TileEntityMultiBlockPart<TankMultiBlock>(TankMultiBlock::class, ::TankMultiBlock) {
    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (hasCapability(capability, facing)) {
            val fluidComp = multiblockController!!.controllerTileEntity!!.fluidComp
            if (fluidComp.isAttached) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast<T>(
                        multiblockController!!.controllerTileEntity!!.fluidComp.innerComponent.fluid)
            }
        }
        return null
    }
}