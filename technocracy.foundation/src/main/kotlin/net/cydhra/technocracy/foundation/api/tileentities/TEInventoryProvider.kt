package net.cydhra.technocracy.foundation.api.tileentities

import net.cydhra.technocracy.foundation.content.capabilities.AbstractComponentCapabilityBridge
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.item.ItemStack

/**
 * A tile entity that provides inventory should implement this in order to be compatible to
 * [net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryCapability]. However, the implementing
 * class does not have to be a tile entity. Rather, any class that handles inventory-content validation and/or wants
 * to be notified about inventory updates may implement this. Under no circumstances should there be any assumptions
 * about the nature of an implementor of this interface.
 */
interface TEInventoryProvider<T: AbstractComponentCapabilityBridge> {

    /**
     * @param inventory the handler that is requesting whether the slot is valid
     * @param slot inventory slot
     * @param stack that shall be inserted into the slot
     *
     * @return true, if the stack can be inserted into the slot
     */
    fun isItemValid(inventory: T, slot: Int, stack: ItemStack): Boolean

    /**
     * @param inventory the handler that got its slot updated
     * @param slot inventory slot
     * @param stack the new ItemStack in the slot
     */
    fun onSlotUpdate(inventory: T, slot: Int, stack: ItemStack, originalStack: ItemStack)
}