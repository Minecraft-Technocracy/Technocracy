package net.cydhra.technocracy.foundation.client.gui.machine.tabs

import net.cydhra.technocracy.foundation.client.gui.machine.Tab
import net.minecraft.client.Minecraft

class WipTab(width: Int, height: Int) : Tab(width, height) {
    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().fontRenderer.drawString("This is tab has yet to be made", this.height / 2, 0, -1)
    }

    override fun update() {

    }
}
