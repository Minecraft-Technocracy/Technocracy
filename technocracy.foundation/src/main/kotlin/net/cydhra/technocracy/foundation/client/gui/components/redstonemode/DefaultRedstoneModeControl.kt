package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager

class DefaultRedstoneModeControl(posX: Int, posY: Int, val component: RedstoneModeComponent, val gui: TCGui) : RedstoneModeControl(posX, posY) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)
        val clr = if(hovered) 0.7f else 1f
        GlStateManager.color(clr, clr, clr, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, component.redstoneMode.ordinal * 16f, 59f, width, height, 256f, 256f)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "Redstone Mode: ${component.redstoneMode.name}"
        gui.renderTooltip(mutableListOf(str), mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        component.redstoneMode = RedstoneModeComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeComponent.RedstoneMode.values().size]
    }
}