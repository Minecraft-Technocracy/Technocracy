package net.cydhra.technocracy.foundation.capabilities.fluid

import net.cydhra.technocracy.foundation.items.capability.AbstractItemCapabilityComponent
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.capability.IFluidHandlerItem


class DynamicFluidHandlerItem(val stack: ItemStack, capacity: Int, allowedFluid: MutableList<String>, tanktype: TankType) : DynamicFluidHandler(capacity, allowedFluid, tanktype), IFluidHandlerItem {
    override fun getContainer(): ItemStack {
        return (componentParent as AbstractItemCapabilityComponent).wrapper.stack
    }
}