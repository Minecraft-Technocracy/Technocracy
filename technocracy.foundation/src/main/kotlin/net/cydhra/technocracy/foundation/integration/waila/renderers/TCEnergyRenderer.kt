package net.cydhra.technocracy.foundation.integration.waila.renderers

import mcp.mobius.waila.api.IWailaCommonAccessor
import mcp.mobius.waila.api.IWailaTooltipRenderer
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.awt.Dimension

class TCEnergyRenderer : IWailaTooltipRenderer {

    override fun getSize(params: Array<String>, accessor: IWailaCommonAccessor): Dimension {
        return Dimension(55 + Minecraft.getMinecraft().fontRenderer.getStringWidth(getEnergyString(Integer.valueOf(params[0]), Integer.valueOf(params[1]))), 10)
    }

    override fun draw(params: Array<String>, accessor: IWailaCommonAccessor) { // code is pretty ugly, needs a recode later
        val currentEnergy = Integer.valueOf(params[0])
        val maxEnergy = Integer.valueOf(params[1])
        val level = currentEnergy.toFloat() / maxEnergy.toFloat()

        GlStateManager.color(1F, 1F, 1F, 1F)
        Minecraft.getMinecraft().textureManager.bindTexture(TCClientGuiImpl.guiComponents)
        GlStateManager.enableBlend()
        GlStateManager.rotate(90f, 0f, 0f, 1f)
        GlStateManager.color(0.4f, 0.4f, 0.4f, 1f)
        val posX = 0
        val posY = -50
        for(i in 0 until 10) {
            drawModalRectWithCustomSizedTexture(posX, posY + (9 - i) * 5, posX + 10, posY + (9 - i) * 5 + 5, 45f, 0f, 256f, 256f)
        }
        if (level > 0f) {
            GlStateManager.color(1f, 1f, 1f, 1f)
            for (i in 0 until (level * 10).toInt()) {
                drawModalRectWithCustomSizedTexture(posX, posY + (9 - i) * 5, posX + 10, posY + (9 - i) * 5 + 5, 45f, 0f, 256f, 256f)
            }
            GlStateManager.disableBlend()
        }
        GlStateManager.rotate(-90f, 0f, 0f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(getEnergyString(currentEnergy, maxEnergy), 55f, 1f, 0xa0a0a0, false)

    }

    fun getEnergyString(current: Int, max: Int): String {
        return "${current}RF/${max}RF"
    }

    fun drawModalRectWithCustomSizedTexture(left: Int, top: Int, right: Int, bottom: Int, texX: Float, texY: Float, textureWidth: Float, textureHeight: Float) { // TODO make this function globally available
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