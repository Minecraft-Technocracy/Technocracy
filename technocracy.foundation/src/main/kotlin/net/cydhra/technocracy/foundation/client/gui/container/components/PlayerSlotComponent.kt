package net.cydhra.technocracy.foundation.client.gui.container.components

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot


class PlayerSlotComponent(inventoryIn: IInventory, index: Int) : Slot(inventoryIn, index, -1, -1), IContainerSlot {
    override val isPlayerInventory = true
    override var type = DynamicInventoryCapability.InventoryType.BOTH
    override var enabled = true

    override fun isEnabled(): Boolean {
        return super.isEnabled() && enabled
    }
}