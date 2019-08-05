package net.cydhra.technocracy.foundation.capabilities.fluid

import net.cydhra.technocracy.foundation.capabilities.AbstractDynamicHandler
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

/**
 * @param capacity fluid storage capacity
 * @param allowedFluid a list of fluid names that are allowed to store, empty if all fluids are allowed
 * @param tanktype whether input, output or both are allowed
 */
class DynamicFluidHandler(var capacity: Int = 1000, val allowedFluid: MutableList<String>,
                          var tanktype: TankType = TankType.BOTH) :
        IFluidHandler, INBTSerializable<NBTTagCompound>, AbstractDynamicHandler() {

    var currentFluid: FluidStack? = null
        private set

    val simpleTankProperty = arrayOf<IFluidTankProperties>(SimpleTankProperty(this))

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {

        val lastFluid = currentFluid

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

        if (doDrain) {
            markDirty(currentFluid != lastFluid)
        }

        return out
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        val lastFluid = currentFluid

        if (currentFluid == null)
            return null

        val drain = Math.min(maxDrain, currentFluid!!.amount)

        if (doDrain) {
            currentFluid!!.amount -= drain
        }

        val out = FluidStack(currentFluid!!.fluid, drain)

        if (currentFluid!!.amount == 0) {
            currentFluid = null
        }

        if (doDrain) {
            markDirty(currentFluid != lastFluid)
        }

        return out
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        val lastFluid = currentFluid

        if (!allowedFluid.contains(resource.fluid.name) && !allowedFluid.isEmpty()) {
            return 0
        }

        if (currentFluid != null && !currentFluid!!.isFluidEqual(resource)) {
            return 0
        }


        if (doFill) {
            if (currentFluid == null) {
                currentFluid = FluidStack(resource.fluid, 0)
            }

            val fill = Math.min(resource.amount, capacity - currentFluid!!.amount)

            currentFluid!!.amount += fill

            markDirty(currentFluid != lastFluid)

            return fill
        } else {
            if (currentFluid == null) {
                return Math.min(resource.amount, capacity)
            }

            return Math.min(resource.amount, capacity - currentFluid!!.amount)
        }
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return simpleTankProperty
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        currentFluid = FluidStack.loadFluidStackFromNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound {
        val tag = NBTTagCompound()
        tag.setInteger("Amount", this.currentFluid?.amount ?: 0)
        tag.setInteger("Capacity", this.capacity)
        tag.setString("FluidName", if (currentFluid != null) FluidRegistry.getFluidName(this.currentFluid) else "")
        return tag
    }

    enum class TankType {
        INPUT, OUTPUT, BOTH
    }
}