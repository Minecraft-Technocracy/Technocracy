package net.cydhra.technocracy.foundation.content.capabilities.fluid

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidTankProperties


class SimpleTankProperty(val fluidHandler: DynamicFluidCapability) : IFluidTankProperties {
    override fun canDrainFluidType(fluidStack: FluidStack?): Boolean {
        return fluidHandler.allowedFluid.isEmpty() || fluidHandler.allowedFluid.contains(fluidStack!!.fluid.name)
    }

    override fun getContents(): FluidStack? {
        return fluidHandler.currentFluid
    }

    override fun canFillFluidType(fluidStack: FluidStack?): Boolean {
        return fluidHandler.allowedFluid.isEmpty() || fluidHandler.allowedFluid.contains(fluidStack!!.fluid.name)
    }

    override fun getCapacity(): Int {
        return fluidHandler.capacity
    }

    override fun canFill(): Boolean {
        return fluidHandler.tanktype != DynamicFluidCapability.TankType.OUTPUT
    }

    override fun canDrain(): Boolean {
        return fluidHandler.tanktype != DynamicFluidCapability.TankType.INPUT
    }
}