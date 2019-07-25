package net.cydhra.technocracy.foundation.integration.waila.renderers

import mcp.mobius.waila.api.IWailaCommonAccessor
import mcp.mobius.waila.api.IWailaTooltipRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import java.awt.Dimension

class TCFluidRenderer: IWailaTooltipRenderer {

    override fun getSize(params: Array<String>, accessor: IWailaCommonAccessor): Dimension {
        return Dimension(55 + Minecraft.getMinecraft().fontRenderer.getStringWidth(getBarSuffix(params)), 10)
    }

    override fun draw(params: Array<String>, accessor: IWailaCommonAccessor) {
        val amount = Integer.valueOf(params[0])
        val capacity = Integer.valueOf(params[1])
        val level = amount.toFloat() / capacity.toFloat()

        GlStateManager.color(1F, 1F, 1F, 1F)
        Gui.drawRect(0, 0,50, 10, Color.black.rgb)

        if(level > 0f) {
            GlStateManager.color(1f, 1f, 1f, 1f)
            Gui.drawRect(0, 0, (50 * level).toInt(), 10, Color.blue.rgb)
        }

        Minecraft.getMinecraft().fontRenderer.drawString("${amount}mb", 3, 1, 0xffffff)
        Minecraft.getMinecraft().fontRenderer.drawString(getBarSuffix(params), 55, 1, 0xffffff)
    }

    fun getBarSuffix(params: Array<String>): String {
        return params[2]
    }
}