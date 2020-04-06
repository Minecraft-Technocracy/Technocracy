package net.cydhra.technocracy.foundation.client.gui.components.label

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft

class DefaultLabel(posX: Int, posY: Int, text: String, val color: Int = 0xffffff, val shadow: Boolean = true, override var gui: TCGui) :
        Label(posX, posY, text) {

    override var width: Int = 0
    override var height: Int = 9

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(width == 0 && text.isNotBlank()) width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
        Minecraft.getMinecraft().fontRenderer.drawString(text, posX.toFloat() + x, posY.toFloat() + y, color, shadow)
    }
}
