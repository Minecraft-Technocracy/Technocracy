package net.cydhra.technocracy.foundation.client.gui.components.label

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11


class WrappingLabel(posX: Int, posY: Int, val maxWidth: Int, val scaling: Double, text: String, val color: Int = 0xffffff, val shadow: Boolean = true, override var gui: TCGui) :
        Label(posX, posY, text) {

    override var width: Int = maxWidth
    override var height: Int = (9 / scaling).toInt()

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glTranslated(posX + x.toDouble(), posY + y.toDouble(), 0.0)
        GL11.glScaled(scaling, scaling, scaling)
        Minecraft.getMinecraft().fontRenderer.drawSplitString(text, 0, 0, (maxWidth / scaling).toInt(), -1)
        GL11.glScaled(1 / scaling, 1 / scaling, 1 / scaling)
        GL11.glTranslated(-(posX + x.toDouble()), -(posY + y.toDouble()), 0.0)
    }
}