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
 * @param provider an instance of [TEInventoryProvider] that is handling update events to this component's inventory
 * @param facing the tile entity facing, where this inventory is accessible from
 * @param inventoryType the IO-type of inventory. For automation purposes, input and output slots are in different
 * inventories (so they can be accessed from different faces of a machine block).
 */
class InventoryTileEntityComponent(
        size: Int,
        provider: TEInventoryProvider,
        val facing: MutableSet<EnumFacing>,
        val inventoryType: DynamicInventoryCapability.InventoryType = DynamicInventoryCapability.InventoryType.BOTH) :
        AbstractCapabilityTileEntityComponent() {

    /**
     * Secondary constructor that takes only one facing
     *
     * @see [InventoryTileEntityComponent]
     */
    constructor(
            size: Int,
            provider: TEInventoryProvider,
            facing: EnumFacing,
            inventoryType: DynamicInventoryCapability.InventoryType = DynamicInventoryCapability.InventoryType.BOTH)
            : this(size, provider, mutableSetOf(facing), inventoryType)

    /**
     * Inventory capability of the machine
     */
    val inventory: DynamicInventoryCapability = DynamicInventoryCapability(size, provider,
            (0..size).map { it to inventoryType }.toMap().toMutableMap())

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