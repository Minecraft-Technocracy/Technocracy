package net.cydhra.technocracy.foundation.client.gui.components.slot

import net.cydhra.technocracy.foundation.client.gui.components.ITCComponent
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability

/**
 * Common interface to GUI item slots. Since two different kinds of slots are required, because player-inventories
 * and capability-inventories are not compatible, common behaviour is collected here.
 */
interface ITCSlot : ITCComponent {
    var internal_enabled: Boolean
    val isPlayerInventory: Boolean
    val index: Int
    var type: DynamicInventoryCapability.InventoryType
}