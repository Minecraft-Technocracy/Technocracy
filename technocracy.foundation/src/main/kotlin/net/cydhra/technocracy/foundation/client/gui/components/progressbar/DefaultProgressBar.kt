package net.cydhra.technocracy.foundation.client.gui.components.progressbar

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager

class DefaultProgressBar(posX: Int, posY: Int, orientation: Orientation) : ProgressBar(posX, posY, orientation) {
    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)

        GlStateManager.color(1F, 1F, 1F, 1F)

        Gui.drawModalRectWithCustomSizedTexture(posX - 1, posY, 0F, 28F, width, height, 256F, 256F)

        if (progress > 0) {
            Gui.drawModalRectWithCustomSizedTexture(posX - 1,
                    posY - 1,
                    0F,
                    28F + height,
                    (width * progress).toInt() + 1,
                    height + 1,
                    256F,
                    256F)
        }
    }
}