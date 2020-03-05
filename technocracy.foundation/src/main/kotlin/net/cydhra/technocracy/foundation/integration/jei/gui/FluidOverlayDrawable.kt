package net.cydhra.technocracy.foundation.integration.jei.gui

import mezz.jei.api.gui.IDrawable
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.FluidMeter
import net.minecraft.client.Minecraft


class FluidOverlayDrawable(val meter: FluidMeter) : IDrawable {
    override fun getHeight(): Int {
        return meter.height
    }

    override fun draw(minecraft: Minecraft, xOffset: Int, yOffset: Int) {
        meter.drawOverlay(xOffset - meter.posX -1, yOffset - meter.posY -1)
    }

    override fun getWidth(): Int {
        return meter.width
    }
}