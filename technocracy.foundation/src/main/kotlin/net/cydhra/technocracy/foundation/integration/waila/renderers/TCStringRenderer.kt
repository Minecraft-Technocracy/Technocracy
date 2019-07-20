package net.cydhra.technocracy.foundation.integration.waila.renderers

import mcp.mobius.waila.api.IWailaCommonAccessor
import mcp.mobius.waila.api.IWailaTooltipRenderer
import net.minecraft.client.Minecraft
import java.awt.Dimension


class TCStringRenderer : IWailaTooltipRenderer {

    override fun getSize(params: Array<String>, acessor: IWailaCommonAccessor): Dimension {
        val x = Integer.valueOf(params[1])
        val y = Integer.valueOf(params[2])
        return Dimension(x + Minecraft.getMinecraft().fontRenderer.getStringWidth(params[0]), y + if(params[0] == "") 0 else Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT)
    }

    override fun draw(params: Array<String>, accessor: IWailaCommonAccessor) {
        val x = Integer.valueOf(params[1])
        val y = Integer.valueOf(params[2])
        Minecraft.getMinecraft().fontRenderer.drawString(params[0], x.toFloat(), y.toFloat(), 0xA0A0A0, true)
    }

}