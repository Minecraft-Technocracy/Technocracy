package net.cydhra.technocracy.foundation.client.gui.components.energymeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.util.Interpolator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.roundToInt

class DefaultEnergyMeter(posX: Int, posY: Int, val component: EnergyStorageTileEntityComponent, val gui: TCGui) : EnergyMeter(posX, posY) {

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1F, 1F, 1F, 1F)

        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        //edge top left
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y, TCGui.slotCornerTopLeft.x.toFloat(), TCGui.slotCornerTopLeft.y.toFloat(), TCGui.slotCornerTopLeft.width, TCGui.slotCornerTopLeft.height, 256f, 256f)
        //edge top right
        Gui.drawModalRectWithCustomSizedTexture(posX + x + width - TCGui.slotCornerTopRight.width, posY + y, TCGui.slotCornerTopRight.x.toFloat(), TCGui.slotCornerTopRight.y.toFloat(), TCGui.slotCornerTopRight.width, TCGui.slotCornerTopRight.height, 256f, 256f)

        //edge bottom left
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y + height - TCGui.slotCornerBottomLeft.height, TCGui.slotCornerBottomLeft.x.toFloat(), TCGui.slotCornerBottomLeft.y.toFloat(), TCGui.slotCornerBottomLeft.width, TCGui.slotCornerBottomLeft.height, 256f, 256f)
        //edge bottom right
        Gui.drawModalRectWithCustomSizedTexture(posX + x + width - TCGui.slotCornerBottomRight.width, posY + y + height - TCGui.slotCornerBottomRight.height, TCGui.slotCornerBottomRight.x.toFloat(), TCGui.slotCornerBottomRight.y.toFloat(), TCGui.slotCornerBottomRight.width, TCGui.slotCornerBottomRight.height, 256f, 256f)

        //line top
        Gui.drawScaledCustomSizeModalRect(posX + x + TCGui.slotCornerTopLeft.width, posY + y,
                TCGui.slotLineTop.x.toFloat(), TCGui.slotLineTop.y.toFloat(),
                TCGui.slotLineTop.width, TCGui.slotLineTop.height,
                width - TCGui.slotCornerTopLeft.width - TCGui.slotCornerTopRight.width, TCGui.slotLineTop.height,
                256f, 256f)
        //line bottom
        Gui.drawScaledCustomSizeModalRect(posX + x + TCGui.slotCornerBottomLeft.width, posY + y + height - TCGui.slotLineBottom.height,
                TCGui.slotLineBottom.x.toFloat(), TCGui.slotLineBottom.y.toFloat(),
                TCGui.slotLineBottom.width, TCGui.slotLineBottom.height,
                width - TCGui.slotCornerBottomLeft.width - TCGui.slotCornerBottomRight.width, TCGui.slotLineBottom.height,
                256f, 256f)

        //line left
        Gui.drawScaledCustomSizeModalRect(posX + x, posY + y + TCGui.slotCornerTopLeft.height,
                TCGui.slotLineLeft.x.toFloat(), TCGui.slotLineLeft.y.toFloat(),
                TCGui.slotLineLeft.width, TCGui.slotLineLeft.height,
                TCGui.slotLineLeft.width, height - TCGui.slotCornerBottomLeft.height - TCGui.slotCornerBottomRight.height,
                256f, 256f)

        //line right
        Gui.drawScaledCustomSizeModalRect(posX + x + width - TCGui.slotLineRight.width, posY + y + TCGui.slotCornerTopRight.width,
                TCGui.slotLineRight.x.toFloat(), TCGui.slotLineRight.y.toFloat(),
                TCGui.slotLineRight.width, TCGui.slotLineRight.height,
                TCGui.slotLineRight.width, height - TCGui.slotCornerBottomLeft.height - TCGui.slotCornerBottomRight.height,
                256f, 256f)

        //fill
        Gui.drawScaledCustomSizeModalRect(posX + x + TCGui.slotCornerTopLeft.width / 2, posY + y + TCGui.slotCornerTopRight.width / 2,
                TCGui.slotContent.x.toFloat(), TCGui.slotContent.y.toFloat(),
                TCGui.slotContent.width, TCGui.slotContent.height,
                width - TCGui.slotCornerBottomLeft.width, height - TCGui.slotCornerBottomRight.height,
                256f, 256f)


        val level = Interpolator.linearInterpolate(lastLevel, this.level, partialTicks)


        val interpolR = MathHelper.clamp(Interpolator.linearInterpolate(107.0, 255.0, level.toFloat()).toInt(), 0, 255)
        val interpolG = MathHelper.clamp(Interpolator.linearInterpolate(14.0, 35.0, level.toFloat()).toInt(), 0, 255)
        val interpolB = MathHelper.clamp(Interpolator.linearInterpolate(19.0, 39.0, level.toFloat()).toInt(), 0, 255)

        drawGradientRect(posX + x + 1.0, posY + y + 1.0 + ((height - 1) * (1 - level)), posX + x + width - 1.0, posY + y + height - 1.0, Color(interpolR, interpolG, interpolB).rgb, Color(107, 14, 19).rgb)
        GlStateManager.disableBlend()
    }

    fun drawGradientRect(left: Double, top: Double, right: Double, bottom: Double, startColor: Int, endColor: Int) {
        val f = (startColor shr 24 and 255).toFloat() / 255.0f
        val f1 = (startColor shr 16 and 255).toFloat() / 255.0f
        val f2 = (startColor shr 8 and 255).toFloat() / 255.0f
        val f3 = (startColor and 255).toFloat() / 255.0f
        val f4 = (endColor shr 24 and 255).toFloat() / 255.0f
        val f5 = (endColor shr 16 and 255).toFloat() / 255.0f
        val f6 = (endColor shr 8 and 255).toFloat() / 255.0f
        val f7 = (endColor and 255).toFloat() / 255.0f
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.shadeModel(7425)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(right, top, 0.0).color(f1, f2, f3, f).endVertex()
        bufferbuilder.pos(left, top, 0.0).color(f1, f2, f3, f).endVertex()
        bufferbuilder.pos(left, bottom, 0.0).color(f5, f6, f7, f4).endVertex()
        bufferbuilder.pos(right, bottom, 0.0).color(f5, f6, f7, f4).endVertex()
        tessellator.draw()
        GlStateManager.shadeModel(7424)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
    }

    override fun update() {
        lastLevel = if (lastLevel != -1.0) level else component.energyStorage.currentEnergy / component.energyStorage.capacity.toDouble()
        level = component.energyStorage.currentEnergy / component.energyStorage.capacity.toDouble()
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "${(level * component.energyStorage.capacity).roundToInt()}RF/${component.energyStorage.capacity}RF"
        gui.drawHoveringText(mutableListOf(str), mouseX, mouseY)
    }
}