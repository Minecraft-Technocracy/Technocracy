package net.cydhra.technocracy.foundation.client.gui.container.components

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler


class SlotComponent(val handler: IItemHandler, index: Int, override var type: DynamicInventoryCapability.InventoryType = DynamicInventoryCapability.InventoryType.BOTH) : SlotItemHandler(handler, index, -1, -1), IContainerSlot {
    override var enabled = true
    override val isPlayerInventory = false

    override fun isEnabled(): Boolean {
        return enabled
    }
}