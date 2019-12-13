package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesComponent
import net.minecraft.entity.player.EntityPlayer

class MachineUpgradesTab(parent: TCGui,
                         private val upgrades: MachineUpgradesComponent,
                         private val player: EntityPlayer?) : TCTab("Upgrades", parent) {

    override fun init() {
        if (player != null) {
            addPlayerInventorySlots(player, 8, 84)
        }


    }

}