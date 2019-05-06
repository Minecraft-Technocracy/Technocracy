package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot


open class TCContainer : Container() {

    init {
//        val inventory = machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)
//        addSlotToContainer(object : SlotItemHandler(inventory, 0, 80, 35) {
//            override fun onSlotChanged() {
//                machine.markDirty()
//            }
//        })
//        for (row in 0..2) {
//            for (slot in 0..8) {
//                this.addSlotToContainer(TCSlot(Minecraft.getMinecraft().player.inventory, slot + row * 9 + 9, 8 + slot * 18,
//                        84 + row *
//                        18))
//            }
//        }
//
//        for (k in 0..8) {
//            this.addSlotToContainer(TCSlot(this.playerInventory, k, 8 + k * 18, 142))
//        }
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
