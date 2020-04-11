package net.cydhra.technocracy.foundation.content.capabilities.fluid

import net.cydhra.technocracy.foundation.model.items.capability.AbstractItemComponent
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.capability.IFluidHandlerItem


class DynamicItemFluidStorage(val stack: ItemStack, capacity: Int, allowedFluid: MutableList<String>, tanktype: TankType) : DynamicFluidCapability(capacity, allowedFluid, tanktype), IFluidHandlerItem {
    override fun getContainer(): ItemStack {
        return (componentParent as AbstractItemComponent).wrapper.stack
    }
}