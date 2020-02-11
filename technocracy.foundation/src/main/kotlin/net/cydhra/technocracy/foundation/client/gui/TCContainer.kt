package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack


open class TCContainer(val machineInputs: Int = 0, val machineOutputs: Int = 0) : Container() {

    private val playerInventorySize = machineInputs + machineOutputs + 26
    private val playerHotBarStart = playerInventorySize + 1
    private val playerHotBarEnd = playerHotBarStart + 8

    override fun transferStackInSlot(player: EntityPlayer, index: Int): ItemStack? {
        var newStack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot.hasStack) {
            val oldStack = slot.stack
            newStack = oldStack.copy()

            if (index < machineInputs + machineOutputs) { // take from machine
                if (!this.mergeItemStack(oldStack, machineInputs + machineOutputs, playerHotBarEnd + 1, true)) {
                    return ItemStack.EMPTY
                }

                slot.onSlotChange(oldStack, newStack)
            } else { // place into machine (if possible; otherwise place elsewhere in inventory)
                if (index in machineInputs until playerHotBarStart) {
                    if (!this.mergeItemStack(oldStack, 0, machineInputs, false) && !this.mergeItemStack(oldStack, playerHotBarStart, playerHotBarEnd + 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index >= playerHotBarStart && index < playerHotBarEnd + 1) {
                    if (!this.mergeItemStack(oldStack, 0, machineInputs, false) && !this.mergeItemStack(oldStack, machineInputs + machineOutputs, playerInventorySize + 1, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (oldStack.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (oldStack.count == newStack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, oldStack)
        }

        return newStack
    }

    fun clearComponents() {
        this.inventorySlots.clear()
    }

    fun registerComponent(component: TCComponent) {
        if (component is Slot) {
            this.addSlotToContainer(component)
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}
