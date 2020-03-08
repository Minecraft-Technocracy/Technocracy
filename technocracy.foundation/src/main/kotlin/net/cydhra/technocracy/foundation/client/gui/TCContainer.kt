package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.ITCComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.slot.ITCSlot
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import kotlin.streams.toList


open class TCContainer : Container() {


    private val components = mutableListOf<TCComponent>()

    lateinit var gui: TCGui

    /**
     * The tileentity this gui belongs to if there is one
     */
    var tileEntity: TileEntity? = null

    override fun transferStackInSlot(player: EntityPlayer, index: Int): ItemStack? {
        var newStack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]
        val tcSlot = slot as ITCSlot

        val tmp = inventorySlots.stream().filter { (it as ITCSlot).isPlayerInventory }.toList()
        val playerHotbar = tmp.filter { it.slotIndex < 9 }.map { it.slotNumber }.toList()
        val playerUpperInv = tmp.filter { it.slotIndex >= 9 }.map { it.slotNumber }.toList()
        val playerInvWhole = tmp.map { it.slotNumber }.toList()

        val guiSlots = inventorySlots.stream().filter { !(it as ITCSlot).isPlayerInventory }.filter { (it as ITCSlot).isEnabled() }.mapToInt { it.slotNumber }.toList()

        if (slot.hasStack) {
            val oldStack = slot.stack.copy()
            newStack = oldStack.copy()

            if (!tcSlot.isPlayerInventory) { // take from machine
                if (!this.mergeItemStack(oldStack, playerInvWhole, true)) {
                    return ItemStack.EMPTY
                }
                slot.onSlotChange(oldStack, newStack)
            } else { // place into machine (if possible; otherwise place elsewhere in inventory)
                if (!this.mergeItemStack(oldStack, guiSlots, false)) {
                    if (playerHotbar.contains(index)) {
                        if (!this.mergeItemStack(oldStack, playerUpperInv, false)) {
                            return ItemStack.EMPTY
                        }
                    } else {
                        if (!this.mergeItemStack(oldStack, playerHotbar, false)) {
                            return ItemStack.EMPTY
                        }
                    }
                }
            }

            if (oldStack.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.putStack(oldStack)
            }

            if (oldStack.count == newStack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, oldStack)
        }

        return newStack
    }

    /**
     * Copy of the vanilla mergeItemStack method but now checks if slot is valid for merging
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    fun mergeItemStack(stack: ItemStack, indices: List<Int>, reverseDirection: Boolean): Boolean {

        val list = if (reverseDirection) indices.reversed() else indices

        var flag = false

        if (stack.isStackable) {
            for (i in list) {
                if (stack.isEmpty)
                    break

                val slot = inventorySlots[i]
                val itemstack = slot.stack
                if (!itemstack.isEmpty && itemstack.item === stack.item && (!stack.hasSubtypes || stack.metadata == itemstack.metadata) && ItemStack.areItemStackTagsEqual(stack, itemstack) && (slot as ITCSlot).type != DynamicInventoryCapability.InventoryType.OUTPUT) {
                    val j = itemstack.count + stack.count
                    val maxSize = slot.getItemStackLimit(stack).coerceAtMost(stack.maxStackSize)

                    if (j <= maxSize) {
                        stack.count = 0
                        itemstack.count = j
                        slot.onSlotChanged()
                        flag = true
                    } else if (itemstack.count < maxSize) {
                        stack.shrink(maxSize - itemstack.count)
                        itemstack.count = maxSize
                        slot.onSlotChanged()
                        flag = true
                    }
                }
            }
        }
        if (!stack.isEmpty) {
            for (i in list) {
                if (stack.isEmpty)
                    break

                val slot1 = inventorySlots[i]
                val itemstack1 = slot1.stack
                if (itemstack1.isEmpty && slot1.isItemValid(stack) && (slot1 as ITCSlot).type != DynamicInventoryCapability.InventoryType.OUTPUT) {
                    if (stack.count > slot1.getItemStackLimit(stack)) {
                        slot1.putStack(stack.splitStack(slot1.getItemStackLimit(stack)))
                    } else {
                        slot1.putStack(stack.splitStack(stack.count))
                    }
                    slot1.onSlotChanged()
                    flag = true
                    break
                }
            }
        }
        return flag
    }

    fun registerComponent(component: ITCComponent) {
        if (component is Slot) {
            this.addSlotToContainer(component)
        } else if (component is TCComponent) {
            component.componentId = components.size
            components.add(component)
        }
    }

    fun clickComponent(player: EntityPlayer, componentId: Int, clickType: Int) {
        for (comp in components) {
            if (comp.componentId == componentId) {
                comp.handleClientClick(player, clickType)
            }
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}
