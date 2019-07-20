package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.roundToInt

class DefaultFluidMeter(posX: Int, posY: Int, val component: FluidComponent, val gui: TCGui) : FluidMeter(posX, posY) {

    var flow: Int = 0

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()
        if (level > 0f) {
            /*if(component.fluid.currentFluid == null) {
                Gui.drawRect(posX, ((1f - level) * height).toInt() + posY, posX + width, posY + height, Color(0.3f, 0.3f, 0.9f).rgb)
                return
            }*/

            //val fluid: Fluid = component.fluid.currentFluid!!.fluid
            val fluid: Fluid = net.cydhra.technocracy.foundation.liquids.general.acrylicAcidFluid // placeholder
            val color = Color(fluid.color)

            GlStateManager.color(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
            Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation(fluid.flowing.resourceDomain, "textures/${fluid.flowing.resourcePath}.png"))
            drawModalRectWithCustomSizedTexture(posX, ((1f - level) * height).toInt() + posY, posX + width, posY + height, 11f, flow.toFloat(), 32f, 1024f)
        }

        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, if(component.fluid.tanktype == DynamicFluidHandler.TankType.INPUT) 10f else 0f, 75f, width, height, 256f, 256f)

        GlStateManager.disableBlend()
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val str = "${(level * component.fluid.capacity).roundToInt()}mb/${component.fluid.capacity}mb"
        gui.renderTooltip(mutableListOf(str), mouseX, mouseY)
    }

    override fun update() {
        super.update()
        flow++
        if (flow > 1024)
            flow = 0
    }

    fun drawModalRectWithCustomSizedTexture(left: Int, top: Int, right: Int, bottom: Int, texX: Float, texY: Float, textureWidth: Float, textureHeight: Float) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(left.toDouble(), bottom.toDouble(), 0.0).tex((texX * f).toDouble(), ((top - bottom + texY) * f1).toDouble()).endVertex()
        bufferbuilder.pos(right.toDouble(), bottom.toDouble(), 0.0).tex(((right - left + texX) * f).toDouble(), ((top - bottom + texY) * f1).toDouble()).endVertex()
        bufferbuilder.pos(right.toDouble(), top.toDouble(), 0.0).tex(((right - left + texX) * f).toDouble(), (texY * f1).toDouble()).endVertex()
        bufferbuilder.pos(left.toDouble(), top.toDouble(), 0.0).tex((texX * f).toDouble(), (texY * f1).toDouble()).endVertex()
        tessellator.draw()
    }

}
