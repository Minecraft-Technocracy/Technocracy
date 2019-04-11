package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityPulverizer : AbstractMachine(DynamicEnergyStorage(capacity = 100, currentEnergy = 100, extractionLimit = 100, receivingLimit = 100)) {

    companion object {
        /**
         * The nbt key where the inventory is stored
         */
        private const val NBT_KEY_INVENTORY = "inventory"
    }

    /**
     * Inventory capability of the pulverizer with input slot (0) and output slot (1)
     */
    private val inventory = ItemStackHandler(2)

    override fun readFromNBT(compound: NBTTagCompound) {
        compound.setTag(NBT_KEY_INVENTORY, inventory.serializeNBT())
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        inventory.deserializeNBT(compound.getCompoundTag(NBT_KEY_INVENTORY))
        return super.writeToNBT(compound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory)
        else
            super.getCapability(capability, facing)
    }

    override fun update() {

    }
}