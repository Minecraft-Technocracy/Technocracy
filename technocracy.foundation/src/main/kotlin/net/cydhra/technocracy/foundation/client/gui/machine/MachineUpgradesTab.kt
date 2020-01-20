package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

class MachineUpgradesTab(parent: TCGui,
                         private val upgrades: MachineUpgradesTileEntityComponent,
                         private val player: EntityPlayer?) :
        TCTab("Upgrades", parent, icon = ResourceLocation("technocracy.foundation", "textures/item/wrench.png")) {

    override fun init() {
        if (player != null) {
            addPlayerInventorySlots(player, 8, parent.guiHeight - 58 - 16 - 5 - 12)
        }

        for (i in 0 until upgrades.numberOfUpgradeSlots) {
            components.add(TCSlotIO(upgrades.inventory, i, 8 + (i % 4) * 18, 20 + (i / 4) * 18, parent))
        }
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(x, y, mouseX, mouseY, partialTicks)

        var deltaY = 20
        val fr = Minecraft.getMinecraft().fontRenderer
        upgrades.description.forEach { (title, data) ->
            parent.drawString(fr, title.formattedText, 85 + x, deltaY + y, 0xffffff)
            parent.drawString(fr, data.formattedText,
                    parent.guiWidth - 15 - fr.getStringWidth(data.unformattedText) + x, deltaY + y, 0xffffff)
            deltaY += 12
        }
    }
}