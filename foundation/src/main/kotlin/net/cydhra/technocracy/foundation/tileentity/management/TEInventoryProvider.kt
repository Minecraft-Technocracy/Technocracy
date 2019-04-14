package net.cydhra.technocracy.foundation.tileentity.management

import net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryHandler
import net.minecraft.item.ItemStack

/**
 * A tile entity that provides inventory should implement this in order to be compatible to
 * [net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryHandler]
 */
interface TEInventoryProvider {

    /**
     * @param inventory the handler that is requesting whether the slot is valid
     * @param slot inventory slot
     * @param stack that shall be inserted into the slot
     *
     * @return true, if the stack can be inserted into the slot
     */
    fun isItemValid(inventory: DynamicInventoryHandler, slot: Int, stack: ItemStack): Boolean
}