package net.cydhra.technocracy.foundation.capabilities.fluid

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties


class DynamicFluidHandler(var capacity: Int = 1000, vararg allowedFluid: Fluid,
                          var tanktype: TankType = TankType.BOTH) :
        IFluidHandler {

    var currentFluid: FluidStack? = null
        private set

    val allowedFluid: MutableList<Fluid> = mutableListOf(*allowedFluid)

    val simpleTankProperty = arrayOf<IFluidTankProperties>(SimpleTankProperty(this))

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        if (currentFluid == null || !currentFluid!!.isFluidEqual(resource)) {
            return null
        }

        val drain = Math.min(resource.amount, currentFluid!!.amount)

        if (doDrain) {
            currentFluid!!.amount -= drain
        }

        val out = FluidStack(currentFluid!!.fluid, drain)

        if (currentFluid!!.amount == 0) {
            currentFluid = null
        }

        return out
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack {
        val drain = Math.min(maxDrain, currentFluid!!.amount)

        if (doDrain) {
            currentFluid!!.amount -= drain
        }

        val out = FluidStack(currentFluid!!.fluid, drain)

        if (currentFluid!!.amount == 0) {
            currentFluid = null
        }

        return out
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {

        if (!allowedFluid.contains(resource.fluid)) {
            return 0
        }

        if (currentFluid != null && !currentFluid!!.isFluidEqual(resource)) {
            return 0
        }

        if (currentFluid == null) {
            currentFluid = FluidStack(resource.fluid, 0)
        }

        val fill = Math.min(resource.amount, capacity - currentFluid!!.amount)

        if (doFill) {
            currentFluid!!.amount += fill
        }

        return fill
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return simpleTankProperty
    }

    enum class TankType {
        INPUT, OUTPUT, BOTH
    }
}