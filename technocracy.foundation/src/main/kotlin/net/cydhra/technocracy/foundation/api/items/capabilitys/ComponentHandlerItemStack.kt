package net.cydhra.technocracy.foundation.api.items.capabilitys

import net.cydhra.technocracy.foundation.content.capabilities.energy.EnergyCapabilityProvider
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import javax.annotation.Nonnull


class ComponentHandlerItemStack : ICapabilityProvider, IComponentHandler {
    override fun hasCapability(@Nonnull capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ItemComponentCapabilityHandler.COMPONENT_HANDLER
    }

    override fun <T> getCapability(@Nonnull capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ItemComponentCapabilityHandler.COMPONENT_HANDLER) ItemComponentCapabilityHandler.COMPONENT_HANDLER.cast<T>(this) else null
    }
}