package net.cydhra.technocracy.foundation.client.gui.components.label

import net.minecraft.client.Minecraft

class DefaultLabel(posX: Int, posY: Int, text: String, val color: Int = 0xffffff, val shadow: Boolean = true) :
        Label(posX, posY, text, Minecraft.getMinecraft().fontRenderer) {

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        fontRenderer.drawString(text, posX.toFloat() + x, posY.toFloat() + y, color, shadow)
    }
}
