package net.cydhra.technocracy.foundation.client.gui.components.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


open class RedRoundButton(posX: Int, posY: Int, width: Int, height: Int, text: String, componentId: Int = -1, clientClick: ((player: EntityPlayer, tileEntity: TileEntity?, button: Int) -> Unit)? = null) :
        Button(posX, posY, width, height, text, Minecraft.getMinecraft().fontRenderer, componentId, clientClick) {

    protected val BUTTON_TEXTURES = ResourceLocation("textures/gui/widgets.png")

    var enabled: Boolean = true

    protected fun getHoverState(mouseOver: Boolean): Int {
        var i = 1
        if (!this.enabled) {
            i = 0
        } else if (mouseOver) {
            i = 2
        }
        return i
    }

    open fun drawTexturedModalRect(x: Int, y: Int, textureX: Int, textureY: Int, width: Int, height: Int) {


        Gui.drawScaledCustomSizeModalRect(x, y, textureX.toFloat(), textureY.toFloat(), 100, 20, width, height, 256f, 256f)

        /*val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos((x).toDouble(), (y + height).toDouble(), 0.0).tex(((textureX).toFloat() * 0.00390625f).toDouble(), ((textureY + height).toFloat() * 0.00390625f).toDouble()).endVertex()
        bufferbuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(((textureX + width).toFloat() * 0.00390625f).toDouble(), ((textureY + height).toFloat() * 0.00390625f).toDouble()).endVertex()
        bufferbuilder.pos((x + width).toDouble(), (y).toDouble(), 0.0).tex(((textureX + width).toFloat() * 0.00390625f).toDouble(), ((textureY).toFloat() * 0.00390625f).toDouble()).endVertex()
        bufferbuilder.pos((x).toDouble(), (y).toDouble(), 0.0).tex(((textureX).toFloat() * 0.00390625f).toDouble(), ((textureY).toFloat() * 0.00390625f).toDouble()).endVertex()
        tessellator.draw()*/
    }

    open fun fillCircle(x: Double, y: Double, r: Double) {
        val tessellator = Tessellator.getInstance()
        val renderer = tessellator.buffer
        GlStateManager.disableCull()
        renderer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
        val rad = Math.PI / 180
        for (i in 0..360) {
            val x2 = sin(i * rad) * r
            val y2 = cos(i * rad) * r
            val x3 = sin((i - 1) * rad) * r
            val y3 = cos((i - 1) * rad) * r
            renderer.pos(x + x3, y + y3, 0.0).endVertex()
            renderer.pos(x + x2, y + y2, 0.0).endVertex()
            renderer.pos(x, y, 0.0).endVertex()
        }
        tessellator.draw()
    }

    open fun drawCenteredString(fontRendererIn: FontRenderer, text: String, x: Int, y: Int, color: Int) {
        fontRendererIn.drawString(text, (x - fontRendererIn.getStringWidth(text) / 2).toFloat(), y.toFloat(), color, false)
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        val posX2 = posX + width / 2.0
        val posY2 = posY + (height + 10) / 2.0

        val distA = sqrt((posX2 - mouseX).pow(2.0) + (posY2 - mouseY).pow(2.0))
        val distB = sqrt((posX2 - mouseX).pow(2.0) + (posY2 - 10 - mouseY).pow(2.0))

        return distA <= 50 || distB <= 50
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

        GlStateManager.bindTexture(0)
        GlStateManager.disableTexture2D()

        val posX = posX + x
        val posY = posY + y

        val hovered = isMouseOnComponent(mouseX - x, mouseY - y)

        val down = Mouse.isButtonDown(0) && hovered

        val posX2 = posX + width / 2.0
        val posY2 = posY + (height + 10) / 2.0

        Gui.drawRect(posX2.toInt() - 53, posY2.toInt() - 53, posX2.toInt() + 53, posY2.toInt() + 53, 0xFF_A0_A0_A0.toInt())
        GlStateManager.color(184 / 255f, 47 / 255f, 42 / 255f)
        fillCircle(posX2, posY2, 50.0)
        GlStateManager.color(1f, 65 / 255f, 58 / 255f)
        fillCircle(posX2, posY2 - if (down) 2 else 10, 50.0)

        var j = 14737632

        if (!this.enabled) {
            j = 10526880
        } else if (hovered) {
            j = 16777120
        }

        drawCenteredString(fontRenderer, text, posX + width / 2, posY2.toInt() - (if (down) 2 else 10) - fontRenderer.FONT_HEIGHT / 2, -1)

    }
}