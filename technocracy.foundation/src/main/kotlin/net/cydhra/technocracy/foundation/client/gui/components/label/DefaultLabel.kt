package net.cydhra.technocracy.foundation.client.gui.components.label

import net.minecraft.client.Minecraft

class DefaultLabel(posX: Int, posY: Int, text: String, val color: Int = 0xffffff):Label(posX, posY, text, Minecraft.getMinecraft().fontRenderer) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        fontRenderer.drawString(text, posX, posY, color)
    }
}