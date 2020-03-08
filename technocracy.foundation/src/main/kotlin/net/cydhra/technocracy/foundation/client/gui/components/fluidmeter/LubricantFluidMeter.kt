package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.*


class LubricantFluidMeter(posX: Int, posY: Int, val component: FluidTileEntityComponent, val gui: TCGui) : FluidMeter(posX, posY) {
    companion object {
        val lubTexture: ResourceLocation = ResourceLocation("technocracy.foundation", "textures/gui/lubricant_addon.png")
    }

    override var width = 36
    override var height = 64

    override fun drawOverlay(x: Int, y: Int) {
    }

    override fun drawBackground(x: Int, y: Int) {
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

        val col = if (component.fluid.capacity == 0) 0.5f else 1f

        GlStateManager.color(col, col, col, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(lubTexture)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(x + posX, y + posY, 28f, 0f, width, height, 64f, 64f)

        Minecraft.getMinecraft().textureManager.bindTexture(tankTexture)
        Gui.drawModalRectWithCustomSizedTexture(x + posX + 6, y + posY + 7, 0f, 0f, 16, 50, 64f, 64f)

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

                    val croppedWidth = 16 - 1.0
                    val croppedHeight = 50 - 1.0

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

                            bufferbuilder.setTranslation(6.0, 7.0, 0.0)

                            bufferbuilder.pos(xCoord, yCoord + heightIn, 0.0).tex(minU, maxV).endVertex()
                            bufferbuilder.pos(xCoord + widthIn, yCoord + heightIn, 0.0).tex(maxU, maxV).endVertex()
                            bufferbuilder.pos(xCoord + widthIn, yCoord, 0.0).tex(maxU, minV).endVertex()
                            bufferbuilder.pos(xCoord, yCoord, 0.0).tex(minU, minV).endVertex()
                            tessellator.draw()
                        }
                    }
                }
            }
        }

        Minecraft.getMinecraft().textureManager.bindTexture(tankTexture)
        Gui.drawModalRectWithCustomSizedTexture(x + posX + 6, y + posY + 7, 32f, 0f, 16, 50, 64f, 64f)
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX + 6 && mouseX < posX + 6 + 16 && mouseY > posY + 7 && mouseY < posY + 50 + 7
    }

    override fun update() {
        level = if (component.fluid.currentFluid != null) component.fluid.currentFluid!!.amount.toFloat() / component.fluid.capacity.toFloat() else 0f
        this.level = MathHelper.clamp(this.level, 0F, 1F)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        drawToolTip(component, mouseX, mouseY, gui)
    }

}