package net.cydhra.technocracy.foundation.client.gui.container.components

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability


interface IContainerSlot : IContainerComponent {
    fun isEnabled(): Boolean
    val isPlayerInventory: Boolean
    var type: DynamicInventoryCapability.InventoryType
    var enabled: Boolean
}