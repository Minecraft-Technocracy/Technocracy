package net.cydhra.technocracy.foundation.client.gui.components.progressbar

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressTileEntityComponent
import net.cydhra.technocracy.foundation.util.Interpolator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import kotlin.math.roundToInt

class DefaultProgressBar(posX: Int, posY: Int, orientation: Orientation, val component: ProgressTileEntityComponent?, override var gui: TCGui) : ProgressBar(posX, posY, orientation) {
    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCClientGuiImpl.guiComponents)

        GlStateManager.color(1F, 1F, 1F, 1F)

        val progress = Interpolator.linearInterpolate(lastprogress, progress, partialTicks)

        when (this.orientation) {
            Orientation.LEFT -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1 + x, posY + y, width.toFloat(), 28F, width, height, 256F, 256F)

                if (progress > 0) {
                    drawModalRectWithCustomSizedTexture(posX - 1.0 + width + x,
                            posY + height + y.toDouble(),
                            43F,
                            58F,
                            -width * progress,
                            -height.toDouble(),
                            256F,
                            256F)
                }
            }
            Orientation.RIGHT -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1 + x, posY + y, 0F, 28F, width, height, 256F, 256F)

                if (progress > 0) {
                    drawModalRectWithCustomSizedTexture(posX - 1.0 + x,
                            posY - 1.0 + y,
                            0F,
                            28F + height,
                            width * progress + 1.0,
                            height + 1.0,
                            256F,
                            256F)
                }
            }
            Orientation.UP -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1 + x, posY + y, 60F, 37F, height, width, 256F, 256F)

                if (progress > 0) {
                    drawModalRectWithCustomSizedTexture(posX - 1.0 + height + x,
                            posY + width + y.toDouble(),
                            59F,
                            58F,
                            -height.toDouble(),
                            -width * progress,
                            256F,
                            256F)
                }
            }
            Orientation.DOWN -> {
                Gui.drawModalRectWithCustomSizedTexture(posX - 1 + x, posY + y, 44F, 15F, height, width, 256F, 256F)

                if (progress > 0) {
                    drawModalRectWithCustomSizedTexture(posX - 1.0 + x,
                            posY - 1.0 + y,
                            59F,
                            15F,
                            height.toDouble(),
                            width * progress,
                            256F,
                            256F)
                }
            }
        }
    }

    fun drawModalRectWithCustomSizedTexture(x: Double, y: Double, u: Float, v: Float, width: Double, height: Double, textureWidth: Float, textureHeight: Float) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(x, (y + height), 0.0).tex((u * f).toDouble(), ((v + height.toFloat()) * f1).toDouble()).endVertex()
        bufferbuilder.pos((x + width), (y + height), 0.0).tex(((u + width.toFloat()) * f).toDouble(), ((v + height.toFloat()) * f1).toDouble()).endVertex()
        bufferbuilder.pos((x + width), y, 0.0).tex(((u + width.toFloat()) * f).toDouble(), (v * f1).toDouble()).endVertex()
        bufferbuilder.pos(x, y, 0.0).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
        tessellator.draw()
    }

    override fun update() {
        if (component != null) {
            lastprogress = progress
            progress = component.progress / 100.0
            if (progress == 0.0)
                lastprogress = 0.0
        }
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        if (gui is TCClientGuiImpl) {
            val str = "${(progress * 100).roundToInt()}%"
            (gui as TCClientGuiImpl).drawHoveringText(mutableListOf(str), mouseX, mouseY)
        }
    }

    fun getDrawable(ticksPerCycle: Int, guiHelper: IGuiHelper): IDrawable {
        return object : IDrawable {

            private val timer = guiHelper.createTickTimer(ticksPerCycle, 100, false)

            override fun draw(mc: Minecraft, x: Int, y: Int) {
                this@DefaultProgressBar.lastprogress = this@DefaultProgressBar.progress
                this@DefaultProgressBar.progress = timer.value / timer.maxValue.toDouble()
                this@DefaultProgressBar.draw(0, 0, -1, -1, mc.renderPartialTicks)
            }

            override fun getWidth(): Int {
                return this@DefaultProgressBar.width
            }

            override fun getHeight(): Int {
                return this@DefaultProgressBar.height
            }
        }
    }
}