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


open class TCContainer : Container() {


    private val components = mutableListOf<TCComponent>()

    /**
     * The tileentity this gui belongs to if there is one
     */
    var tileEntity: TileEntity? = null

    override fun transferStackInSlot(player: EntityPlayer, index: Int): ItemStack? {
        var newStack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]
        val tcSlot = slot as ITCSlot

        val firstPlayerSlot = inventorySlots.stream().filter { (it as ITCSlot).isPlayerInventory }.mapToInt { it.slotNumber }.findFirst().orElseGet { -1 }

        val playerInventorySize = firstPlayerSlot + 26
        val playerHotBarStart = playerInventorySize + 1
        val playerHotBarEnd = playerHotBarStart + 8

        if (slot.hasStack) {
            val oldStack = slot.stack
            newStack = oldStack.copy()

            if (!tcSlot.isPlayerInventory) { // take from machine
                if (!this.mergeItemStack(oldStack, firstPlayerSlot, playerHotBarEnd + 1, true)) {
                    return ItemStack.EMPTY
                }

                slot.onSlotChange(oldStack, newStack)
            } else { // place into machine (if possible; otherwise place elsewhere in inventory)
                if (!this.mergeItemStack(oldStack, 0, firstPlayerSlot, false)) {

                    if (index >= playerHotBarStart) {
                        if (!this.mergeItemStack(oldStack, firstPlayerSlot, playerHotBarStart, false)) {
                            return ItemStack.EMPTY
                        }
                    } else {
                        if (!this.mergeItemStack(oldStack, playerHotBarStart, playerHotBarEnd + 1, false)) {
                            return ItemStack.EMPTY
                        }
                    }
                }


                /*if (index in machineInputs until playerHotBarStart) {
                    if (!this.mergeItemStack(oldStack, 0, machineInputs, false) && !this.mergeItemStack(oldStack, playerHotBarStart, playerHotBarEnd + 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index >= playerHotBarStart && index < playerHotBarEnd + 1) {
                    if (!this.mergeItemStack(oldStack, 0, machineInputs, false) && !this.mergeItemStack(oldStack, machineInputs + machineOutputs, playerInventorySize + 1, false)) {
                        return ItemStack.EMPTY
                    }
                }*/
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

    /**
     * Copy of the vanilla mergeItemStack method but now checks if slot is valid for merging
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    override fun mergeItemStack(stack: ItemStack, startIndex: Int, endIndex: Int, reverseDirection: Boolean): Boolean {
        var flag = false
        var i = startIndex
        if (reverseDirection) {
            i = endIndex - 1
        }
        if (stack.isStackable) {
            while (!stack.isEmpty) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break
                    }
                } else if (i >= endIndex) {
                    break
                }
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
                if (reverseDirection) {
                    --i
                } else {
                    ++i
                }
            }
        }
        if (!stack.isEmpty) {
            i = if (reverseDirection) {
                endIndex - 1
            } else {
                startIndex
            }
            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break
                    }
                } else if (i >= endIndex) {
                    break
                }
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
                if (reverseDirection) {
                    --i
                } else {
                    ++i
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
