package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotContent
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotCornerBottomLeft
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotCornerBottomRight
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotCornerTopLeft
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotCornerTopRight
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotLineBottom
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotLineLeft
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotLineRight
import net.cydhra.technocracy.foundation.client.gui.TCGui.Companion.slotLineTop
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.*


class DefaultFluidMeter(posX: Int, posY: Int, val component: FluidTileEntityComponent, val gui: TCGui, val type: Int = 0) : FluidMeter(posX, posY) {

    init {
        if (type == 1)
            width = 12
    }

    private var flowAnimation: Int = 0

    private var linecount = 10

    fun setLineCount(count: Int): DefaultFluidMeter {
        linecount = count
        return this
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

        val col = if (component.fluid.capacity == 0) 0.5f else 1f

        GlStateManager.color(col, col, col, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(tankTexture)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        Gui.drawModalRectWithCustomSizedTexture(x + posX, y + posY, if (type == 1) 16f else 0f, 0f, 16, 50, 64f, 64f)

        /*
        //edge top left
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y, slotCornerTopLeft.x.toFloat(), slotCornerTopLeft.y.toFloat(), slotCornerTopLeft.width, slotCornerTopLeft.height, 256f, 256f)
        //edge top right
        Gui.drawModalRectWithCustomSizedTexture(posX + x + width - slotCornerTopRight.width, posY + y, slotCornerTopRight.x.toFloat(), slotCornerTopRight.y.toFloat(), slotCornerTopRight.width, slotCornerTopRight.height, 256f, 256f)

        //edge bottom left
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y + height - slotCornerBottomLeft.height, slotCornerBottomLeft.x.toFloat(), slotCornerBottomLeft.y.toFloat(), slotCornerBottomLeft.width, slotCornerBottomLeft.height, 256f, 256f)
        //edge bottom right
        Gui.drawModalRectWithCustomSizedTexture(posX + x + width - slotCornerBottomRight.width, posY + y + height - slotCornerBottomRight.height, slotCornerBottomRight.x.toFloat(), slotCornerBottomRight.y.toFloat(), slotCornerBottomRight.width, slotCornerBottomRight.height, 256f, 256f)

        //line top
        Gui.drawScaledCustomSizeModalRect(posX + x + slotCornerTopLeft.width, posY + y,
                slotLineTop.x.toFloat(), slotLineTop.y.toFloat(),
                slotLineTop.width, slotLineTop.height,
                width - slotCornerTopLeft.width - slotCornerTopRight.width, slotLineTop.height,
                256f, 256f)
        //line bottom
        Gui.drawScaledCustomSizeModalRect(posX + x + slotCornerBottomLeft.width, posY + y + height - slotLineBottom.height,
                slotLineBottom.x.toFloat(), slotLineBottom.y.toFloat(),
                slotLineBottom.width, slotLineBottom.height,
                width - slotCornerBottomLeft.width - slotCornerBottomRight.width, slotLineBottom.height,
                256f, 256f)

        //line left
        Gui.drawScaledCustomSizeModalRect(posX + x, posY + y + slotCornerTopLeft.height,
                slotLineLeft.x.toFloat(), slotLineLeft.y.toFloat(),
                slotLineLeft.width, slotLineLeft.height,
                slotLineLeft.width, height - slotCornerBottomLeft.height - slotCornerBottomRight.height,
                256f, 256f)

        //line right
        Gui.drawScaledCustomSizeModalRect(posX + x + width - slotLineRight.width, posY + y + slotCornerTopRight.width,
                slotLineRight.x.toFloat(), slotLineRight.y.toFloat(),
                slotLineRight.width, slotLineRight.height,
                slotLineRight.width, height - slotCornerBottomLeft.height - slotCornerBottomRight.height,
                256f, 256f)

        //fill
        Gui.drawScaledCustomSizeModalRect(posX + x + slotCornerTopLeft.width / 2, posY + y + slotCornerTopRight.width / 2,
                slotContent.x.toFloat(), slotContent.y.toFloat(),
                slotContent.width, slotContent.height,
                width - slotCornerBottomLeft.width, height - slotCornerBottomRight.height,
                256f, 256f)*/

        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer

        if (level > 0f) {
            if (component.fluid.currentFluid != null) {
                val fluid: Fluid = component.fluid.currentFluid!!.fluid
                val color = Color(fluid.color)

                GlStateManager.color(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
                val sprite = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(fluid.still.toString())

                if (sprite != null) {
                    Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

                    val croppedWidth = width - 1.0
                    val croppedHeight = height - 1.0

                    val mw = floor(croppedWidth / sprite.iconWidth.toDouble()).toInt()
                    val mh = floor(croppedHeight * level / sprite.iconHeight.toDouble()).toInt()

                    for (w in 0..mw) {
                        for (h in 0..mh) {
                            val xCoord = min(posX + x + 1.0 + sprite.iconWidth * (w + 1), posX + x + croppedWidth)
                            val widthIn = posX + x + 1.0 + sprite.iconWidth * w - xCoord
                            val yCoord = posY + y + croppedHeight - sprite.iconHeight * h
                            val heightIn = max(posY + y + croppedHeight - sprite.iconHeight * (h + 1.0), posY + y + 1 + (croppedHeight * (1.0 - level))) - yCoord

                            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)

                            val maxU = sprite.minU.toDouble()
                            val minU = sprite.getInterpolatedU(abs(widthIn)).toDouble()
                            val minV = sprite.minV.toDouble()
                            val maxV = sprite.getInterpolatedV(abs(heightIn)).toDouble()

                            bufferbuilder.pos(xCoord + 0, (yCoord + heightIn), 0.0).tex(minU, maxV).endVertex()
                            bufferbuilder.pos((xCoord + widthIn), (yCoord + heightIn), 0.0).tex(maxU, maxV).endVertex()
                            bufferbuilder.pos((xCoord + widthIn), (yCoord + 0), 0.0).tex(maxU, minV).endVertex()
                            bufferbuilder.pos((xCoord + 0), (yCoord + 0), 0.0).tex(minU, minV).endVertex()
                            tessellator.draw()
                        }
                    }
                }
            }
        }

        Minecraft.getMinecraft().textureManager.bindTexture(tankTexture)
        Gui.drawModalRectWithCustomSizedTexture(x + posX, y + posY, 32f + if (type == 1) 16f else 0f, 0f, 16, 50, 64f, 64f)

        //drawOverlay(x,y)


    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        drawToolTip(component, mouseX, mouseY, gui)
    }

    override fun update() {
        level = if (component.fluid.currentFluid != null) component.fluid.currentFluid!!.amount.toFloat() / component.fluid.capacity.toFloat() else 0f
        this.level = MathHelper.clamp(this.level, 0F, 1F)
    }

    override fun drawOverlay(x: Int, y: Int) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer

        val lines = height / linecount.toDouble()
        for (i in 1 until linecount) {

            val left = 2.0
            val top = lines * i

            var right = (width - 2) / 2.0

            if (i == linecount / 2)
                right = width - 3.0

            val bottom = top + 1.0

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
            bufferbuilder.pos(left + posX + x, bottom + posY + y, 0.0).color(86, 0, 0, 255).endVertex()
            bufferbuilder.pos(right + posX + x, bottom + posY + y, 0.0).color(86, 0, 0, 255).endVertex()
            bufferbuilder.pos(right + posX + x, top + posY + y, 0.0).color(86, 0, 0, 255).endVertex()
            bufferbuilder.pos(left + posX + x, top + posY + y, 0.0).color(86, 0, 0, 255).endVertex()
            tessellator.draw()
        }
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun drawBackground(x: Int, y: Int) {
        val col = if (component.fluid.capacity == 0) 0.5f else 1f

        GlStateManager.color(col, col, col, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        //edge top left
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y, slotCornerTopLeft.x.toFloat(), slotCornerTopLeft.y.toFloat(), slotCornerTopLeft.width, slotCornerTopLeft.height, 256f, 256f)
        //edge top right
        Gui.drawModalRectWithCustomSizedTexture(posX + x + width - slotCornerTopRight.width, posY + y, slotCornerTopRight.x.toFloat(), slotCornerTopRight.y.toFloat(), slotCornerTopRight.width, slotCornerTopRight.height, 256f, 256f)

        //edge bottom left
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y + height - slotCornerBottomLeft.height, slotCornerBottomLeft.x.toFloat(), slotCornerBottomLeft.y.toFloat(), slotCornerBottomLeft.width, slotCornerBottomLeft.height, 256f, 256f)
        //edge bottom right
        Gui.drawModalRectWithCustomSizedTexture(posX + x + width - slotCornerBottomRight.width, posY + y + height - slotCornerBottomRight.height, slotCornerBottomRight.x.toFloat(), slotCornerBottomRight.y.toFloat(), slotCornerBottomRight.width, slotCornerBottomRight.height, 256f, 256f)

        //line top
        Gui.drawScaledCustomSizeModalRect(posX + x + slotCornerTopLeft.width, posY + y,
                slotLineTop.x.toFloat(), slotLineTop.y.toFloat(),
                slotLineTop.width, slotLineTop.height,
                width - slotCornerTopLeft.width - slotCornerTopRight.width, slotLineTop.height,
                256f, 256f)
        //line bottom
        Gui.drawScaledCustomSizeModalRect(posX + x + slotCornerBottomLeft.width, posY + y + height - slotLineBottom.height,
                slotLineBottom.x.toFloat(), slotLineBottom.y.toFloat(),
                slotLineBottom.width, slotLineBottom.height,
                width - slotCornerBottomLeft.width - slotCornerBottomRight.width, slotLineBottom.height,
                256f, 256f)

        //line left
        Gui.drawScaledCustomSizeModalRect(posX + x, posY + y + slotCornerTopLeft.height,
                slotLineLeft.x.toFloat(), slotLineLeft.y.toFloat(),
                slotLineLeft.width, slotLineLeft.height,
                slotLineLeft.width, height - slotCornerBottomLeft.height - slotCornerBottomRight.height,
                256f, 256f)

        //line right
        Gui.drawScaledCustomSizeModalRect(posX + x + width - slotLineRight.width, posY + y + slotCornerTopRight.width,
                slotLineRight.x.toFloat(), slotLineRight.y.toFloat(),
                slotLineRight.width, slotLineRight.height,
                slotLineRight.width, height - slotCornerBottomLeft.height - slotCornerBottomRight.height,
                256f, 256f)

        //fill
        Gui.drawScaledCustomSizeModalRect(posX + x + slotCornerTopLeft.width / 2, posY + y + slotCornerTopRight.width / 2,
                slotContent.x.toFloat(), slotContent.y.toFloat(),
                slotContent.width, slotContent.height,
                width - slotCornerBottomLeft.width, height - slotCornerBottomRight.height,
                256f, 256f)
    }
}
