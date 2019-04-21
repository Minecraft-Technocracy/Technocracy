package net.cydhra.technocracy.foundation.capabilities.fluid

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidTankProperties


class SimpleTankProperty(val fluidHandler: DynamicFluidHandler) : IFluidTankProperties {
    override fun canDrainFluidType(fluidStack: FluidStack?): Boolean {
        return fluidHandler.allowedFluid.contains(fluidStack!!.fluid)
    }

    override fun getContents(): FluidStack? {
        return fluidHandler.currentFluid
    }

    override fun canFillFluidType(fluidStack: FluidStack?): Boolean {
        return fluidHandler.allowedFluid.contains(fluidStack!!.fluid)
    }

    override fun getCapacity(): Int {
        return fluidHandler.capacity
    }

    override fun canFill(): Boolean {
        return fluidHandler.tanktype != DynamicFluidHandler.TankType.OUTPUT
    }

    override fun canDrain(): Boolean {
        return fluidHandler.tanktype != DynamicFluidHandler.TankType.INPUT
    }
}