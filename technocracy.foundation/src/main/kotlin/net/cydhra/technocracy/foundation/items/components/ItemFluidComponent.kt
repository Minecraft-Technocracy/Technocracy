package net.cydhra.technocracy.foundation.items.components

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandlerItem
import net.cydhra.technocracy.foundation.items.capability.AbstractItemCapabilityComponent
import net.cydhra.technocracy.foundation.tileentity.components.ComponentType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


class ItemFluidComponent(val fluid: DynamicFluidHandlerItem) : AbstractItemCapabilityComponent(fluid.stack) {
    override val type: ComponentType = ComponentType.FLUID

    init {
        fluid.componentParent = this
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(fluid)
        else
            null
    }

    override fun serializeNBT(): NBTTagCompound {
        return fluid.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        fluid.deserializeNBT(nbt)
    }
}