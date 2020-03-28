package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


/**
 * A machine component that offers a fluid inventory for the machine. It also implements the fluid capability
 */
class FluidTileEntityComponent(var fluid: DynamicFluidCapability, override val facing: MutableSet<EnumFacing>) :
        AbstractDirectionalCapabilityTileEntityComponent() {

    constructor(fluid: DynamicFluidCapability, facing: EnumFacing) : this(fluid, mutableSetOf(facing))

    init {
        fluid.componentParent = this
    }

    override val type: ComponentType = ComponentType.FLUID

    override fun getDirection(): Direction {
        return when (fluid.tanktype) {
            DynamicFluidCapability.TankType.INPUT -> Direction.INPUT
            DynamicFluidCapability.TankType.OUTPUT -> Direction.OUTPUT
            else -> Direction.BOTH
        }
    }

    /**
     * @param size amount of fluid in millibucket
     * @param allowedFluid list of allowed input fluids
     * @param tanktype type of tank [DynamicFluidCapability.TankType]
     * @param facing facing of the capability
     */
    constructor(capacity: Int = 1000, vararg allowedFluid: String = arrayOf(),
                tanktype: DynamicFluidCapability.TankType = DynamicFluidCapability.TankType.BOTH,
                facing: MutableSet<EnumFacing>)
            : this(DynamicFluidCapability(capacity, mutableListOf(*allowedFluid), tanktype), facing)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.facing.contains(facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluid)
        else
            null
    }

    override fun serialize(): NBTTagCompound {
        return fluid.serializeNBT()
    }

    override fun deserialize(nbt: NBTTagCompound) {
        fluid.deserializeNBT(nbt)
    }
}