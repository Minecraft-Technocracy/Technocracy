package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.roundToInt

class DefaultFluidMeter(posX:Int, posY:Int, val component: FluidComponent, val gui: TCGui): FluidMeter(posX, posY) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Gui.drawRect(posX, posY, posX + width, posY + height, Color(0.3f, 0.3f, 0.3f).rgb)
        if (level > 0f) {
            Gui.drawRect(posX, ((1f - level) * height).toInt() + posY, posX + width, posY + height, if(component.fluid.currentFluid != null) component.fluid.currentFluid!!.fluid.color else Color(0.3f, 0.3f, 0.9f).rgb)
        }
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val str = "${(level * component.fluid.capacity).roundToInt()}mb/${component.fluid.capacity}mb"
        gui.renderTooltip(mutableListOf(str), mouseX, mouseY)
    }

}
