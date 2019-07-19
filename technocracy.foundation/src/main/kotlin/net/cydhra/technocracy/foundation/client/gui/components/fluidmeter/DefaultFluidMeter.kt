package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

class DefaultFluidMeter(posX:Int, posY:Int, val component: FluidComponent): FluidMeter(posX, posY) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Gui.drawRect(posX, posY, posX + width, posY + height, Color(0.3f, 0.3f, 0.3f).rgb)
        if (level > 0f) {
            Gui.drawRect(posX, ((1f - level) * height).toInt() + posY, posX + width, posY + height, if(component.fluid.currentFluid != null) component.fluid.currentFluid!!.fluid.color else Color(0.3f, 0.3f, 0.9f).rgb)
        }
    }

    override fun update() {
        super.update()
    }

}
