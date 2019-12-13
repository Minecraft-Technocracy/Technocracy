package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesComponent
import net.minecraft.entity.player.EntityPlayer

class MachineUpgradesTab(parent: TCGui,
                         private val upgrades: MachineUpgradesComponent,
                         private val player: EntityPlayer?) : TCTab("Upgrades", parent) {

    override fun init() {
        if (player != null) {
            addPlayerInventorySlots(player, 8, 84)
        }

        for (i in 0 until upgrades.numberOfUpgradeSlots) {
            components.add(TCSlotIO(upgrades.inventory, i, 8 + (i % 4) * 18, 20 + (i / 4) * 18, parent))
        }
    }
}