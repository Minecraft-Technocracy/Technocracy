package net.cydhra.technocracy.foundation.client.gui.components.slot

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import javax.annotation.Nonnull


/**
 * Wraps the DynamicInventoryCapability so we can allow Players to extract Items from input slots
 */
class SlotItemHandler(val parent: DynamicInventoryCapability) : IItemHandlerModifiable by parent {

    @Nonnull
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (parent.slotTypes[slot]!! == DynamicInventoryCapability.InventoryType.INPUT && parent.canExtractManually) {
            return parent.extractItem(slot, amount, simulate, true)
        }

        return parent.extractItem(slot, amount, simulate)
    }

}