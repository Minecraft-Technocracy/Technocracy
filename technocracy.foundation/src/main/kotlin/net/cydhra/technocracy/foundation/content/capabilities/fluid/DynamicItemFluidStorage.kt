package net.cydhra.technocracy.foundation.content.capabilities.fluid

import net.cydhra.technocracy.foundation.content.capabilities.IItemCapability
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.capability.IFluidHandlerItem


class DynamicItemFluidStorage(val stack: ItemStack, capacity: Int, allowedFluid: MutableList<String> = mutableListOf(), tanktype: TankType = TankType.BOTH) : DynamicFluidCapability(capacity, allowedFluid, tanktype), IFluidHandlerItem, IItemCapability {
    override fun getContainer(): ItemStack {
        return stack
    }
}