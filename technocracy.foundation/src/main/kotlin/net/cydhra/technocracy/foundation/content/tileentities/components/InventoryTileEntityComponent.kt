package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler

/**
 * A machine component that offers a inventory for the machine. It also implements the inventory capability
 *
 * @param size amount of inventory slots
 */
class InventoryTileEntityComponent(size: Int, provider: TEInventoryProvider, val facing: MutableSet<EnumFacing>) :
        AbstractCapabilityTileEntityComponent() {

    constructor(size: Int, provider: TEInventoryProvider, facing: EnumFacing) : this(size, provider, mutableSetOf(facing))

    /**
     * Inventory capability of the machine
     */
    val inventory: DynamicInventoryCapability = DynamicInventoryCapability(size, provider)

    override val type: ComponentType = ComponentType.INVENTORY

    init {
        inventory.componentParent = this
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.facing.contains(facing)
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
        inventory.deserializeNBT(nbt)
    }
}