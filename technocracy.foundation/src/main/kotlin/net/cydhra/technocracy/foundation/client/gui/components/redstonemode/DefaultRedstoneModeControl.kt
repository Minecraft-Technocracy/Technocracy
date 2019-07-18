package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

class DefaultRedstoneModeControl(posX: Int, posY: Int, val component: RedstoneModeComponent) : RedstoneModeControl(posX, posY) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)
        GlStateManager.color(1F, 1F, 1F, 1F)
        Gui.drawRect(posX, posY, posX + width, posY + height, if (hovered) Color.RED.rgb else Color.BLACK.rgb)
        val str: String = component.redstoneMode.name[0].toString().toUpperCase()
        Minecraft.getMinecraft().fontRenderer.drawString(str, posX + width / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(str) / 2, posY + height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, Color.WHITE.rgb)
    }

    override fun update() {

    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        component.redstoneMode = RedstoneModeComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeComponent.RedstoneMode.values().size]
    }
}