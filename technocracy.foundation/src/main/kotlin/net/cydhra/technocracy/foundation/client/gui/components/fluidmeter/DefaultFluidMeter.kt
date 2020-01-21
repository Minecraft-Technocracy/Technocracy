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
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.roundToInt


class DefaultFluidMeter(posX: Int, posY: Int, val component: FluidTileEntityComponent, val gui: TCGui) : FluidMeter(posX, posY) {

    private var flowAnimation: Int = 0

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

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



        if (level > 0f) {
            if (component.fluid.currentFluid != null) {
                val fluid: Fluid = component.fluid.currentFluid!!.fluid
                val color = Color(fluid.color)

                GlStateManager.color(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
                val sprite = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(fluid.still.toString())
                if (sprite != null) {
                    Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    gui.drawTexturedModalRect(posX + x + 1,
                            ((1f - level) * height).toInt() + posY + y + 1,
                            sprite,
                            width - 2,
                            height - 2 - ((1f - level) * height).toInt())
                }
            }
        }


        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

        val lines = height / 10.0
        GlStateManager.color(86 / 255f, 0f, 0f, 1f)
        for (i in 1..9) {

            val left = 2.0
            val top = lines * i

            var right = (width - 2) / 2.0

            if (i == 5)
                right = width - 3.0

            val bottom = top + 1.0

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION)
            bufferbuilder.pos(left + posX + x, bottom + posY + y, 0.0).endVertex()
            bufferbuilder.pos(right + posX + x, bottom + posY + y, 0.0).endVertex()
            bufferbuilder.pos(right + posX + x, top + posY + y, 0.0).endVertex()
            bufferbuilder.pos(left + posX + x, top + posY + y, 0.0).endVertex()
            tessellator.draw()

        }

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()


        //Gui.drawScaledCustomSizeModalRect(posX + x, posY + y, 0f, 10f, 18, 18, width, 18, 256f, 256f)
        //Gui.drawScaledCustomSizeModalRect(posX + x, posY + y + height - 18, 0f, 10f, 18, 18, width, 18, 256f, 256f)
        //Gui.drawScaledCustomSizeModalRect(posX + x, posY + y + 2, 0f, 12f, 18, 14, width, height - 4, 256f, 256f)


        //Gui.drawRect(posX + x + 18, posY + y, posX + x + 35 + width, posY + y + height, -1)
        //Gui.drawScaledCustomSizeModalRect(posX + x + 35, posY + y, 0f, 12f, 18, 18 , width, height, 256f, 256f)

    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        if (component.fluid.capacity > 0) {
            val str = "${if (component.fluid.currentFluid != null) "${component.fluid.currentFluid?.localizedName}\n§7" else ""}${(level * component.fluid.capacity).roundToInt()}mb/${component.fluid.capacity}mb"
            gui.renderTooltip(mutableListOf(str), mouseX, mouseY)
        }
    }

    override fun update() {
        level = if (component.fluid.currentFluid != null) component.fluid.currentFluid!!.amount.toFloat() / component.fluid.capacity.toFloat() else 0f
        flowAnimation++
        if (flowAnimation > 1024)
            flowAnimation = 0
    }
}
