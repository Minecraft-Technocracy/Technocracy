package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot


open class MachineContainer(protected val playerInventory: InventoryPlayer, protected val machine: MachineTileEntity) :
        Container() {

    init {
//        val inventory = machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)
//        addSlotToContainer(object : SlotItemHandler(inventory, 0, 80, 35) {
//            override fun onSlotChanged() {
//                machine.markDirty()
//            }w
//        })

        for (i in 0..2) {
            for (j in 0..8) {
                addSlotToContainer(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        for (k in 0..8) {
            addSlotToContainer(Slot(playerInventory, k, 8 + k * 18, 142))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }
}
