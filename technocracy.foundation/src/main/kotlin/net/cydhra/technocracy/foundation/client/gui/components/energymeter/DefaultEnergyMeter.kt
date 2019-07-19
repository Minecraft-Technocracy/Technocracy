package net.cydhra.technocracy.foundation.client.gui.components.energymeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.roundToInt

class DefaultEnergyMeter(posX: Int, posY: Int, val component: EnergyStorageComponent, val gui: TCGui) : EnergyMeter(posX, posY) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1F, 1F, 1F, 1F)
        Gui.drawRect(posX, posY, posX + width, posY + height, Color(0.3f, 0.3f, 0.3f).rgb)
        if (level > 0f) {
            Gui.drawRect(posX, ((1f - level) * height).toInt() + posY, posX + width, posY + height, Color(0.9f, 0.2f, 0.2f).rgb)
        }
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val str = "${(level * component.energyStorage.capacity).roundToInt()}RF/${component.energyStorage.capacity}RF"
        gui.renderTooltip(mutableListOf(str), mouseX, mouseY)
    }
}