package net.cydhra.technocracy.foundation.client.gui.components.slot

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class TCSlotIO(val itemHandler: IItemHandler, val index: Int, xPosition: Int, yPosition: Int, gui: TCGui) :
        TCSlot(InventoryBasic("[null]", true, 1) as IInventory, index, xPosition, yPosition, gui) {

    override fun isItemValid(stack: ItemStack): Boolean {
        if (stack.isEmpty || !itemHandler.isItemValid(index, stack))
            return false
        val handler: IItemHandler = this.itemHandler
        val reminder: ItemStack
        if (handler is IItemHandlerModifiable) {
            val currentStack: ItemStack = handler.getStackInSlot(index)
            handler.setStackInSlot(index, ItemStack.EMPTY)
            reminder = handler.insertItem(index, stack, true)
            handler.setStackInSlot(index, currentStack)
        } else {
            reminder = handler.insertItem(index, stack, true)
        }
        return reminder.count < stack.count
    }

    override fun getStack(): ItemStack {
        return itemHandler.getStackInSlot(index)
    }

    override fun putStack(stack: ItemStack) {
        if (itemHandler is IItemHandlerModifiable) {
            this.itemHandler.setStackInSlot(index, stack)
            onSlotChanged()
        }
    }

    override fun onSlotChange(p_75220_1_: ItemStack, p_75220_2_: ItemStack) {

    }

    override fun getSlotStackLimit(): Int {
        return itemHandler.getSlotLimit(index)
    }

    override fun getItemStackLimit(stack: ItemStack): Int {
        val add: ItemStack = stack.copy()
        val max: Int = stack.maxStackSize
        add.count = max
        val currentStack: ItemStack = itemHandler.getStackInSlot(index)

        if(itemHandler is IItemHandlerModifiable) {
            itemHandler.setStackInSlot(index, ItemStack.EMPTY)
            val remainder: ItemStack = itemHandler.insertItem(index, add, true)
            itemHandler.setStackInSlot(index, currentStack)
            return max - remainder.count
        } else {
            val remainder: ItemStack = itemHandler.insertItem(index, add, true)
            return currentStack.count + (max - remainder.count)
        }

    }

    override fun canTakeStack(playerIn: EntityPlayer): Boolean {
        return !itemHandler.extractItem(index, 1, true).isEmpty
    }

    override fun decrStackSize(amount: Int): ItemStack {
        return itemHandler.extractItem(index, amount, false)
    }

    override fun isSameInventory(other: Slot): Boolean {
        return other is TCSlotIO && other.itemHandler == this.itemHandler
    }





}