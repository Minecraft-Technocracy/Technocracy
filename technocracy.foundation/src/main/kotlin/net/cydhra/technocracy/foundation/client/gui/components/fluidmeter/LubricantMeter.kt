package net.cydhra.technocracy.foundation.client.gui.components.fluidmeter

import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.ICompositeComponent
import net.cydhra.technocracy.foundation.client.gui.components.ITCComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractTileEntityDirectionalCapabilityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11


class LubricantMeter(
    override var posX: Int,
    override var posY: Int,
    val component: TileEntityFluidComponent,
    gui: TCGui
) : TCComponent(), ICompositeComponent {
    companion object {
        val lubTexture: ResourceLocation =
            ResourceLocation("technocracy.foundation", "textures/gui/lubricant_addon.png")
    }

    val lubricant = DefaultFluidMeter(6, 7, component, gui)

    override var gui = gui
        set(value) {
            field = value
            lubricant.gui = value
        }

    override var width = 36
    override var height = 64


    override fun drawBackground(x: Int, y: Int) {
        GlStateManager.enableBlend()
        val col = if (component.fluid.capacity == 0) 0.5f else 1f
        GlStateManager.color(col, col, col, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(lubTexture)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(x + posX, y + posY, 28f, 0f, width, height, 64f, 64f)
    }

    override fun drawChildren(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        lubricant.draw(x + posX, y + posY, mouseX, mouseY, partialTicks)
    }

    override fun getElements(): List<Pair<ITCComponent, AbstractTileEntityDirectionalCapabilityComponent>> {
        return listOf(lubricant to component)
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX + 6 && mouseX < posX + 6 + 16 && mouseY > posY + 7 && mouseY < posY + 50 + 7
    }

    override fun update() {
        lubricant.update()
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        when {
            lubricant.isMouseOnComponent(mouseX - posX, mouseY - posY) -> {
                lubricant.drawTooltip(mouseX, mouseY)
            }
            else -> {
                (gui as TCClientGuiImpl).drawHoveringText("Lubricant meter", mouseX, mouseY)
            }
        }
    }

}