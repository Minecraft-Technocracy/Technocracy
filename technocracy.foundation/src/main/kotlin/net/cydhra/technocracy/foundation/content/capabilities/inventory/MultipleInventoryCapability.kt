package net.cydhra.technocracy.foundation.content.capabilities.inventory

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable


class MultipleInventoryCapability(slots: List<DynamicInventoryCapability>) : IItemHandler, IItemHandlerModifiable {

    val slots = mutableListOf<DynamicInventoryCapability>()
    val offsets = mutableMapOf<DynamicInventoryCapability, Int>()
    val size: Int

    init {
        var size = 0
        for (cap in slots) {
            for (s in 0 until cap.slots) {
                this.slots.add(cap)
            }
            offsets[cap] = size
            size += cap.slots
        }

        this.size = size
    }

    override fun getSlots(): Int {
        return size
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        val cap = slots[slot]
        return cap.getStackInSlot(slot - offsets[cap]!!)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val cap = slots[slot]
        return cap.insertItem(slot - offsets[cap]!!, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val cap = slots[slot]
        return cap.extractItem(slot - offsets[cap]!!, amount, simulate)
    }

    override fun getSlotLimit(slot: Int): Int {
        val cap = slots[slot]
        return cap.getSlotLimit(slot - offsets[cap]!!)
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        val cap = slots[slot]
        return cap.setStackInSlot(slot - offsets[cap]!!, stack)
    }
}