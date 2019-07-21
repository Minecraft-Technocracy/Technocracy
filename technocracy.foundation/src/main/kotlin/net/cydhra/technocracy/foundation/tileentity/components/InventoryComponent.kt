package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryHandler
import net.cydhra.technocracy.foundation.tileentity.management.TEInventoryProvider
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler

/**
 * A machine component that offers a inventory for the machine. It also implements the inventory capability
 *
 * @param size amount of inventory slots
 */
class InventoryComponent(size: Int, provider: TEInventoryProvider, val facing: EnumFacing? = null) :
        AbstractCapabilityComponent() {

    override val type: ComponentType = ComponentType.INVENTORY
    /**
     * Inventory capability of the machine
     */
    val inventory = DynamicInventoryHandler(size, provider)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.facing?.equals(facing) ?: true
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory)
        else
            null
    }

    override fun serializeNBT(): NBTTagCompound {
        return inventory.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt as NBTTagCompound)
    }
}