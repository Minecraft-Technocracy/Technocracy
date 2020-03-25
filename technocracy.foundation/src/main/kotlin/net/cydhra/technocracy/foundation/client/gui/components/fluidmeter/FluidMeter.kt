package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.TCCapabilityComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import kotlin.math.roundToInt

abstract class FluidMeter(override var posX: Int, override var posY: Int, component: FluidTileEntityComponent) : TCCapabilityComponent<FluidTileEntityComponent>(component) {

    /**
     * fluid level from 0.0 to 1.0
     */
    var level = 0.0f

    override var width = 16
    override var height = 50

    companion object {
        val tankTexture: ResourceLocation = ResourceLocation("technocracy.foundation", "textures/gui/normaltank.png")
    }

    override fun update() {
        this.level += 0.01F
        this.level = MathHelper.clamp(this.level, 0F, 1F)

        if (level == 1.0F) {
            this.level = 0F
        }
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {

    }

    fun drawToolTip(component: FluidTileEntityComponent, mouseX: Int, mouseY: Int, gui: TCGui) {
        if (component.fluid.capacity > 0) {
            val text = mutableListOf<String>()

            val empty = component.fluid.currentFluid == null
            val color = if (!empty) TextFormatting.GRAY else TextFormatting.WHITE

            if (!empty)
                text.add(component.fluid.currentFluid!!.localizedName + ": ")

            text.add("$color${(level * component.fluid.capacity).roundToInt()}mb / ${component.fluid.capacity}mb")
            gui.drawHoveringText(text, mouseX, mouseY)
        }
    }

    abstract fun drawOverlay(x: Int, y: Int)
    abstract fun drawBackground(x: Int, y: Int)
}
