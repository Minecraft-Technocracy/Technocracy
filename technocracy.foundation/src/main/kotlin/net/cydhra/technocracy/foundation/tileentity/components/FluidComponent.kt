package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


/**
 * A machine component that offers a fluid inventory for the machine. It also implements the fluid capability
 */
class FluidComponent(val fluid: DynamicFluidHandler, val facing: MutableSet<EnumFacing>) :
        AbstractCapabilityComponent() {

    override val type: ComponentType = ComponentType.FLUID

    /**
     * @param size amount of fluid in millibucket
     * @param allowedFluid list of allowed input fluids
     * @param tanktype type of tank [DynamicFluidHandler.TankType]
     * @param facing facing of the capability
     */
    constructor(capacity: Int = 1000, vararg allowedFluid: String = arrayOf(),
                tanktype: DynamicFluidHandler.TankType = DynamicFluidHandler.TankType.BOTH,
                facing: MutableSet<EnumFacing>)
            : this(DynamicFluidHandler(capacity, mutableListOf(*allowedFluid), tanktype), facing)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.facing.contains(facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluid)
        else
            null
    }

    override fun serializeNBT(): NBTTagCompound {
        return fluid.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        fluid.deserializeNBT(nbt as NBTTagCompound)
    }
}