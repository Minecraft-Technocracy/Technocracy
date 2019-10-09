package net.cydhra.technocracy.foundation.conduits.types

import net.cydhra.technocracy.foundation.content.capabilities.energy.EnergyCapabilityProvider
import net.minecraft.util.IStringSerializable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler


enum class PipeType(val unlocalizedName: String, val capability: Capability<*>) :
        IStringSerializable {
    ENERGY("energy", EnergyCapabilityProvider.CAPABILITY_ENERGY!!),
    FLUID("fluid", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY),
    ITEM("item", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    override fun getName(): String {
        return this.unlocalizedName
    }
}