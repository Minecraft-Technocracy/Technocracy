package net.cydhra.technocracy.foundation.client.gui.components.button

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.util.TriConsumer
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


open class DefaultButton(posX: Int, posY: Int, width: Int, height: Int, text: String, override var gui: TCGui, clientClick: ((side: Side, player: EntityPlayer, tileEntity: IAggregatable?, button: Int) -> Unit)? = null) :
        Button(posX, posY, width, height, text, clientClick) {

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


    open fun drawCenteredString(fontRendererIn: FontRenderer, text: String, x: Int, y: Int, color: Int) {
        fontRendererIn.drawStringWithShadow(text, (x - fontRendererIn.getStringWidth(text) / 2).toFloat(), y.toFloat(), color)
    }


    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(BUTTON_TEXTURES)

        var posX = posX + x
        var posY = posY + y

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        val hovered = isMouseOnComponent(mouseX - x, mouseY - y)
        val i: Int = this.getHoverState(hovered) * 20
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

        drawTexturedModalRect(posX, posY, 0, 46 + i, width / 2, height)
        drawTexturedModalRect(posX + width / 2, posY, 100, 46 + i, width / 2, height)

        var j = 14737632

        if (!this.enabled) {
            j = 10526880
        } else if (hovered) {
            j = 16777120
        }

        drawCenteredString(Minecraft.getMinecraft().fontRenderer, text, posX + width / 2, posY + (height - 8) / 2, j)

    }
}