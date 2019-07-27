package net.cydhra.technocracy.foundation.integration.waila.renderers

import mcp.mobius.waila.api.IWailaCommonAccessor
import mcp.mobius.waila.api.IWailaTooltipRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.awt.Dimension

class TCItemRenderer : IWailaTooltipRenderer {

    override fun getSize(params: Array<String>, accessor: IWailaCommonAccessor): Dimension {
        return Dimension(18, 18)
    }

    override fun draw(params: Array<String>, accessor: IWailaCommonAccessor) {
        if (params.size < 2) return
        val item = Item.getByNameOrId(params[0]) ?: return
        val stack = ItemStack(item, Integer.valueOf(params[1]))
        renderItemStack(stack, stack.count.toString())
        Minecraft.getMinecraft().fontRenderer.drawString(stack.displayName, 23, 5, 0xa0a0a0)
    }

    private fun renderItemStack(itm: ItemStack, txt: String) {
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        if (!itm.isEmpty) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0f, 0.0f, 32.0f)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.enableRescaleNormal()
            GlStateManager.enableLighting()
            RenderHelper.enableGUIStandardItemLighting()
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)

            Minecraft.getMinecraft().renderItem.renderItemAndEffectIntoGUI(itm, 0, 0)
            Minecraft.getMinecraft().renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, itm, 0, 0, txt)

            GlStateManager.popMatrix()
            GlStateManager.disableRescaleNormal()
            GlStateManager.disableLighting()
        }
    }

}