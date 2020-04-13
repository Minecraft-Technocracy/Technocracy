package net.cydhra.technocracy.foundation.client.gui.components.energymeter

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.util.Interpolator
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import kotlin.math.roundToInt

class DefaultEnergyMeter(posX: Int, posY: Int, component: TileEntityEnergyStorageComponent, override var gui: TCGui) : EnergyMeter(posX, posY, component) {

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1F, 1F, 1F, 1F)

        Minecraft.getMinecraft().textureManager.bindTexture(TCClientGuiImpl.guiComponents)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        drawModalRectWithCustomSizedTexture(x + posX.toDouble(), y + posY.toDouble(), 75.0, 0.0, width.toDouble(), height.toDouble(), 256f, 256f)

        val level = Interpolator.linearInterpolate(lastLevel, this.level, partialTicks)

        val h = height * (1 - level)

        drawModalRectWithCustomSizedTexture(x + posX.toDouble(), y + posY + h, 75.0 + width, h, width.toDouble(), height - h, 256f, 256f)
    }

    override fun update() {
        lastLevel = if (lastLevel != -1.0) level else component.energyStorage.currentEnergy / component.energyStorage.capacity.toDouble()
        level = component.energyStorage.currentEnergy / component.energyStorage.capacity.toDouble()
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "${(level * component.energyStorage.capacity).roundToInt()}RF / ${component.energyStorage.capacity}RF"
        (gui as TCClientGuiImpl).drawHoveringText(mutableListOf(str), mouseX, mouseY)
    }
}