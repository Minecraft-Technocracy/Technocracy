package net.cydhra.technocracy.foundation.integration.waila.renderers

import mcp.mobius.waila.api.IWailaCommonAccessor
import mcp.mobius.waila.api.IWailaTooltipRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
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

        GlStateManager.translate(0f,0.5f,0f)
        Minecraft.getMinecraft().fontRenderer.drawString("${amount}mb", 3, 1, 0xffffff)
        Minecraft.getMinecraft().fontRenderer.drawString(getBarSuffix(params), 55, 1, 0xa0a0a0)
        GlStateManager.translate(0f,-0.5f,0f)
    }

    fun getBarSuffix(params: Array<String>): String {
        if(params.size < 3) return "" // dunno why, but if there is no fluid there are only 2 elements. might be caused by wailas way of interpreting arguments
        val fluid = FluidRegistry.getFluid(params[2])
        return fluid.getLocalizedName(FluidStack(fluid, 1))
    }
}