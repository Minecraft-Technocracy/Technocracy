package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


/**
 * A machine component that offers a fluid inventory for the machine. It also implements the fluid capability
 *
 * @param size amount of fluid in millibucket
 * @param allowedFluid list of allowed input fluids
 * @param tanktype type of tank [DynamicFluidHandler.TankType]
 * @param facing facing of the capability
 */
class FluidComponent(var capacity: Int = 1000, vararg allowedFluid: Fluid = arrayOf(), var tanktype: DynamicFluidHandler
.TankType = DynamicFluidHandler.TankType.BOTH, val facing: MutableSet<EnumFacing>) :
        AbstractCapabilityComponent() {

    /**
     * Fluid capability of the machine
     */
    val fluid = DynamicFluidHandler(capacity, mutableListOf(*allowedFluid), tanktype)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.facing.contains(facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluid)
        else
            null
    }

    override fun serializeNBT(): NBTBase {
        return fluid.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTBase) {
        fluid.deserializeNBT(nbt as NBTTagCompound)
    }
}