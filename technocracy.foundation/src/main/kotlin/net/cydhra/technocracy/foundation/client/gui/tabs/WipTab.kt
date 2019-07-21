package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.minecraft.client.Minecraft

class WipTab(parent: TCGui) : TCTab("WIP Tab", parent) {
    override fun init() {
        /*components.add(DefaultProgressBar(20, 20, Orientation.RIGHT, parent))
        components.add(DefaultProgressBar(20, 40, Orientation.LEFT, parent))
        components.add(DefaultProgressBar(20, 60, Orientation.UP, parent))
        components.add(DefaultProgressBar(20, 90, Orientation.DOWN, parent))*/
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)

        val machine = (parent.container as MachineContainer).machine
        val str = "You are viewing a " + machine.blockType.localizedName
        Minecraft.getMinecraft().fontRenderer.drawSplitString(str,
                9, 9, 168, -1 and 16579836 shr 2 or (-1 and -16777216))
        Minecraft.getMinecraft().fontRenderer.drawSplitString(str,
                8, 8, 168, -1)

    }

}
