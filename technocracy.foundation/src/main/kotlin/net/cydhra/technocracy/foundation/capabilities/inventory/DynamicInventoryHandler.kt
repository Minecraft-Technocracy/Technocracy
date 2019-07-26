package net.cydhra.technocracy.foundation.capabilities.inventory

import net.cydhra.technocracy.foundation.tileentity.management.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.NonNullList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper


class DynamicInventoryHandler(size: Int = 0, private val machine: TEInventoryProvider) : IItemHandler,
        IItemHandlerModifiable,
        INBTSerializable<NBTTagCompound> {

    var stacks: NonNullList<ItemStack>

    init {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY)
    }

    fun setSize(size: Int) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY)
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        validateSlotIndex(slot)
        this.stacks[slot] = stack
        onContentsChanged(slot)
    }

    override fun getSlots(): Int {
        return stacks.size
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        validateSlotIndex(slot)
        return this.stacks[slot]
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

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
            if (existing.isEmpty) {
                this.stacks[slot] = if (reachedLimit) ItemHandlerHelper.copyStackWithSize(stack, limit) else stack
            } else {
                existing.grow(if (reachedLimit) limit else stack.count)
            }
            onContentsChanged(slot)
        }

        return if (reachedLimit) ItemHandlerHelper.copyStackWithSize(stack, stack.count - limit) else ItemStack.EMPTY
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (amount == 0)
            return ItemStack.EMPTY

        validateSlotIndex(slot)

        val existing = this.stacks[slot]

        if (existing.isEmpty)
            return ItemStack.EMPTY

        val toExtract = Math.min(amount, existing.maxStackSize)

        if (existing.count <= toExtract) {
            if (!simulate) {
                this.stacks[slot] = ItemStack.EMPTY
                onContentsChanged(slot)
            }
            return existing
        } else {
            if (!simulate) {
                this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.count - toExtract)
                onContentsChanged(slot)
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract)
        }
    }

    override fun getSlotLimit(slot: Int): Int {
        return 64
    }

    protected fun getStackLimit(slot: Int, stack: ItemStack): Int {
        return Math.min(getSlotLimit(slot), stack.maxStackSize)
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
        setSize(if (nbt.hasKey("Size", Constants.NBT.TAG_INT)) nbt.getInteger("Size") else stacks.size)
        val tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until tagList.tagCount()) {
            val itemTags = tagList.getCompoundTagAt(i)
            val slot = itemTags.getInteger("Slot")

            if (slot >= 0 && slot < stacks.size) {
                stacks[slot] = ItemStack(itemTags)
            }
        }
        onLoad()
    }

    protected fun validateSlotIndex(slot: Int) {
        if (slot < 0 || slot >= stacks.size)
            throw RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size + ")")
    }

    protected fun onLoad() {

    }

    protected fun onContentsChanged(slot: Int) {

    }
}