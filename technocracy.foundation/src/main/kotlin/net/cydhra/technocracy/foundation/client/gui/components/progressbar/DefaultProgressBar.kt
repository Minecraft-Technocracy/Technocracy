package net.cydhra.technocracy.foundation.client.gui.components.progressbar

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager

class DefaultProgressBar(posX: Int, posY: Int, orientation: Orientation) : ProgressBar(posX, posY, orientation) {
    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)

        GlStateManager.color(1F, 1F, 1F, 1F)

        when (this.orientation) {
            Orientation.LEFT -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1, posY, width.toFloat(), 28F, width, height, 256F, 256F)

                if (progress > 0) {
                    Gui.drawModalRectWithCustomSizedTexture(posX - 1 + width,
                            posY + height,
                            43F,
                            58F,
                            -(width * progress).toInt(),
                            -height,
                            256F,
                            256F)
                }
            }
            Orientation.RIGHT -> {
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
            Orientation.UP -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1, posY, 60F, 37F, height, width, 256F, 256F)

                if (progress > 0) {
                    Gui.drawModalRectWithCustomSizedTexture(posX - 1 + height,
                            posY + width,
                            59F,
                            58F,
                            -height,
                            -(width * progress).toInt(),
                            256F,
                            256F)
                }
            }
            Orientation.DOWN -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1, posY, 44F, 15F, height, width, 256F, 256F)

                if (progress > 0) {
                    Gui.drawModalRectWithCustomSizedTexture(posX - 1,
                            posY - 1,
                            59F,
                            15F,
                            height,
                            (width * progress).toInt(),
                            256F,
                            256F)
                }
            }
        }
    }
}