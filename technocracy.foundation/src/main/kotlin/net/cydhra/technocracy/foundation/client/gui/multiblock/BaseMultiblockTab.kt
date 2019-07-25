package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

abstract class BaseMultiblockTab(val controller: TileEntityMultiBlockPart<*>, parent: TCGui, icon: ResourceLocation) : TCTab(name = controller.blockType.localizedName, parent = parent, icon = icon) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(controller.blockType.localizedName, 8f, 8f, -1, true)
        super.draw(mouseX, mouseY, partialTicks)
    }

}