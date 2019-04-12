package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * A machine component that offers a inventory for the machine. It also implements the inventory capability
 *
 * @param size amount of inventory slots
 */
class InventoryComponent(size: Int) : AbstractCapabilityComponent() {

    /**
     * Inventory capability of the machine
     */
    val inventory = ItemStackHandler(size)

    companion object {
        private const val NBT_KEY_INVENTORY = "inventory"
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory)
        else
            null
    }

    override fun writeToNBT(compound: NBTTagCompound) {
        compound.setTag(NBT_KEY_INVENTORY, inventory.serializeNBT())
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        inventory.deserializeNBT(compound.getCompoundTag(NBT_KEY_INVENTORY))
    }
}