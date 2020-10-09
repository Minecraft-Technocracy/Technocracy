package net.cydhra.technocracy.foundation.content.capabilities.inventory

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.content.capabilities.AbstractComponentCapabilityBridge
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable


/**
 * a capability that can hold an unlimited amount of itemstacks
 */
class DynamicItemHolderCapability(private val machine: TEInventoryProvider<DynamicItemHolderCapability>) : INBTSerializable<NBTTagCompound>, AbstractComponentCapabilityBridge() {

    private var stacks = mutableListOf<ItemStack>()

    fun getStacks(): List<ItemStack> {
        return stacks
    }

    fun removeStack(stack: ItemStack): Boolean {
        val rem = stacks.remove(stack)
        if (rem) {
            machine.onSlotUpdate(this, -1, ItemStack.EMPTY, stack)
            markDirty()
        }
        return rem
    }

    fun addStack(stack: ItemStack): Boolean {
        if (!machine.isItemValid(this, -1, stack)) return false
        val add = stacks.add(stack)
        if (add) {
            machine.onSlotUpdate(this, -1, stack, ItemStack.EMPTY)
            markDirty()
        }
        return add
    }

    fun clear() {
        stacks.clear()
        markDirty()
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbtTagList = NBTTagList()
        for (stack in stacks) {
            val itemTag = NBTTagCompound()
            stack.writeToNBT(itemTag)
            nbtTagList.appendTag(itemTag)
        }
        val nbt = NBTTagCompound()
        nbt.setTag("Items", nbtTagList)
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        stacks.clear()
        val tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until tagList.tagCount()) {
            val itemTags = tagList.getCompoundTagAt(i)
            stacks.add(ItemStack(itemTags))
        }
    }
}