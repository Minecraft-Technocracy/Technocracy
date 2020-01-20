package net.cydhra.technocracy.foundation.client.gui.components.energymeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import kotlin.math.roundToInt

class DefaultEnergyMeter(posX: Int, posY: Int, val component: EnergyStorageTileEntityComponent, val gui: TCGui) : EnergyMeter(posX, posY) {

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1F, 1F, 1F, 1F)
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.enableBlend()
        GlStateManager.color(0.4f, 0.4f, 0.4f, 1f)
        for(i in 0 until 10) {
            drawModalRectWithCustomSizedTexture(posX + x, posY + (9 - i) * 5 + y, posX + width + x, posY + (9 - i) * 5 + 5 + y, 45f, 0f, 256f, 256f)
        }
        if (level > 0f) {
            GlStateManager.color(1f, 1f, 1f, 1f)
            for (i in 0 until (level * 10).toInt()) {
                drawModalRectWithCustomSizedTexture(posX + x, posY + (9 - i) * 5 + y, posX + width + x, posY + (9 - i) * 5 + 5 + y, 45f, 0f, 256f, 256f)
            }
        }
        GlStateManager.disableBlend()
    }

    override fun update() {
        level = component.energyStorage.currentEnergy.toFloat() / component.energyStorage.capacity.toFloat()
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "${(level * component.energyStorage.capacity).roundToInt()}RF/${component.energyStorage.capacity}RF"
        gui.renderTooltip(mutableListOf(str), mouseX, mouseY)
    }

    fun drawModalRectWithCustomSizedTexture(left: Int, top: Int, right: Int, bottom: Int, texX: Float, texY: Float, textureWidth: Float, textureHeight: Float) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(left.toDouble(), bottom.toDouble(), 0.0).tex((texX * f).toDouble(), ((texY + (bottom - top).toFloat()) * f1).toDouble()).endVertex()
        bufferbuilder.pos(right.toDouble(), bottom.toDouble(), 0.0).tex(((texX + (right - left).toFloat()) * f).toDouble(), ((texY + (bottom - top).toFloat()) * f1).toDouble()).endVertex()
        bufferbuilder.pos(right.toDouble(), top.toDouble(), 0.0).tex(((texX + (right - left).toFloat()) * f).toDouble(), (texY * f1).toDouble()).endVertex()
        bufferbuilder.pos(left.toDouble(), top.toDouble(), 0.0).tex((texX * f).toDouble(), (texY * f1).toDouble()).endVertex()
        tessellator.draw()
    }

}