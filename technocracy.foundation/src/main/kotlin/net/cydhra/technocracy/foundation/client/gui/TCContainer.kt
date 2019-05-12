package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack


open class TCContainer(val inventorySize: Int = 0) : Container() {

    private val playerInventorySize = inventorySize + 26
    private val playerHotBarStart = playerInventorySize + 1
    private val playerHotBarEnd = playerHotBarStart + 8

    override fun transferStackInSlot(player: EntityPlayer?, index: Int): ItemStack? {
        var exchangedStack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot.hasStack) {
            val stackInSlot = slot.stack
            exchangedStack = stackInSlot.copy()

            if (index < inventorySize) {
                if (!this.mergeItemStack(stackInSlot, inventorySize, playerHotBarEnd + 1, true)) {
                    return ItemStack.EMPTY
                }

                slot.onSlotChange(stackInSlot, exchangedStack)
            } else {
                if (index in inventorySize until playerHotBarStart) {
                    if (!this.mergeItemStack(stackInSlot, playerHotBarStart, playerHotBarEnd + 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index >= playerHotBarStart && index < playerHotBarEnd + 1) {
                    if (!this.mergeItemStack(stackInSlot, inventorySize, playerInventorySize + 1, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (stackInSlot.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (stackInSlot.count == exchangedStack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, stackInSlot)
        }

        return exchangedStack
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
