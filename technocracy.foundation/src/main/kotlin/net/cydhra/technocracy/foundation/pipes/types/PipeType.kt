package net.cydhra.technocracy.foundation.pipes.types

import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler


enum class PipeType(val unlocalizedName: String, val capability: Capability<*>?,val handler: IPipeHandler) :
        IStringSerializable {
    ENERGY("energy", EnergyCapabilityProvider.CAPABILITY_ENERGY, ItemPipeHandler),
    FLUID("fluid", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ItemPipeHandler),
    ITEM("item", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ItemPipeHandler);

    override fun getName(): String {
        return this.unlocalizedName
    }
}