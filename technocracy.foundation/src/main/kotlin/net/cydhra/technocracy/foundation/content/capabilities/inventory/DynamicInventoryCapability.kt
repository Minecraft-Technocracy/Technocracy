package net.cydhra.technocracy.foundation.content.capabilities.inventory

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.content.capabilities.AbstractComponentCapabilityBridge
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.NonNullList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import kotlin.math.min


class DynamicInventoryCapability(size: Int = 0, private val machine: TEInventoryProvider<DynamicInventoryCapability>, val slotTypes: MutableMap<Int, InventoryType> = mutableMapOf()) : IItemHandler,
        IItemHandlerModifiable,
        INBTSerializable<NBTTagCompound>, AbstractComponentCapabilityBridge() {

    var stacks: NonNullList<ItemStack>

    var size: Int
        get() = stacks.size
        set(value) {
            stacks = NonNullList.withSize(value, ItemStack.EMPTY)
        }

    init {

        stacks = NonNullList.withSize(size, ItemStack.EMPTY)

        for (slot in 0 until size) {
            if (!slotTypes.containsKey(slot))
                slotTypes[slot] = InventoryType.BOTH
        }
    }

    fun forceSlotTypes(type: InventoryType) {
        slotTypes.clear()
        for (slot in 0 until stacks.size) {
            slotTypes[slot] = type
        }
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        validateSlotIndex(slot)
        val original = this.stacks[slot]
        this.stacks[slot] = stack
        onContentsChanged(slot, original)
    }

    /**
     * sets the item without calling [onContentsChanged]
     */
    fun forceStackInSlot(slot: Int, stack: ItemStack) {
        this.stacks[slot] = stack
    }

    override fun getSlots(): Int {
        return stacks.size
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        validateSlotIndex(slot)
        return this.stacks[slot]
    }

    /**
     * Try to insert an item into a given slot
     *
     * @param slot where to try to insert the item
     * @param stack stack to insert
     * @param simulate if true, only the result of this method is simulated, without modifying the actual inventory
     */
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return insertItem(slot, stack, simulate, false)
    }

    /**
     * Try to insert an item into a given slot
     *
     * @param slot where to try to insert the item
     * @param stack stack to insert
     * @param simulate if true, only the result of this method is simulated, without modifying the actual inventory
     * @param forced if true, slot type and checks for item validity are ignored. Used to insert items into output
     * inventories
     */
    fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean, forced: Boolean): ItemStack {
        if (stack.isEmpty)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

        if (!forced && !isItemValid(slot, stack))
            return stack

        if (!forced && slotTypes[slot] == InventoryType.OUTPUT)
            return stack

        val existing = this.stacks[slot]

        var limit = getStackLimit(slot, stack)

        if (!existing.isEmpty) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack

            limit -= existing.count
        }

        if (limit <= 0)
            return stack

        val reachedLimit = stack.count > limit

        if (!simulate) {
            val original = existing.copy()

            if (existing.isEmpty) {
                this.stacks[slot] = if (reachedLimit) ItemHandlerHelper.copyStackWithSize(stack, limit) else stack
            } else {
                existing.grow(if (reachedLimit) limit else stack.count)
            }
            onContentsChanged(slot, original)
        }

        return if (reachedLimit) ItemHandlerHelper.copyStackWithSize(stack, stack.count - limit) else ItemStack.EMPTY
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return extractItem(slot, amount, simulate, false)
    }

    fun extractItem(slot: Int, amount: Int, simulate: Boolean, forced: Boolean): ItemStack {
        if (amount == 0)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

        if (!forced && slotTypes[slot] == InventoryType.INPUT)
            return ItemStack.EMPTY

        val existing = this.stacks[slot]

        if (existing.isEmpty)
            return ItemStack.EMPTY

        val toExtract = min(amount, existing.maxStackSize)

        return if (existing.count <= toExtract) {
            if (!simulate) {
                this.stacks[slot] = ItemStack.EMPTY
                onContentsChanged(slot, existing)
            }
            existing
        } else {
            if (!simulate) {
                this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.count - toExtract)
                onContentsChanged(slot, existing)
            }

            ItemHandlerHelper.copyStackWithSize(existing, toExtract)
        }
    }

    override fun getSlotLimit(slot: Int): Int {
        return 64
    }

    fun getStackLimit(slot: Int, stack: ItemStack): Int {
        val min = min(getSlotLimit(slot), stack.maxStackSize)
        if (machine is CustomItemStackStackLimit)
            return machine.getStackLimit(slot, stack, min)
        return min
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        // callback the machine for that
        return machine.isItemValid(this, slot, stack)
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbtTagList = NBTTagList()
        for (i in stacks.indices) {
            if (!stacks[i].isEmpty) {
                val itemTag = NBTTagCompound()
                itemTag.setInteger("Slot", i)
                stacks[i].writeToNBT(itemTag)
                nbtTagList.appendTag(itemTag)
            }
        }
        val nbt = NBTTagCompound()
        nbt.setTag("Items", nbtTagList)
        nbt.setInteger("Size", stacks.size)
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        size = (if (nbt.hasKey("Size", Constants.NBT.TAG_INT)) nbt.getInteger("Size") else stacks.size)
        val tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until tagList.tagCount()) {
            val itemTags = tagList.getCompoundTagAt(i)
            val slot = itemTags.getInteger("Slot")

            if (slot >= 0 && slot < stacks.size) {
                stacks[slot] = ItemStack(itemTags)
            }
        }
    }

    fun validateSlotIndex(slot: Int) {
        if (slot < 0 || slot >= stacks.size)
            throw RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size + ")")
    }

    private fun onContentsChanged(slot: Int, oldContent: ItemStack) {
        markDirty(true)
        machine.onSlotUpdate(this, slot, stacks[slot], oldContent)
    }

    enum class InventoryType {
        INPUT, OUTPUT, BOTH
    }

    interface CustomItemStackStackLimit {
        fun getStackLimit(slot: Int, stack: ItemStack, default: Int): Int
    }
}