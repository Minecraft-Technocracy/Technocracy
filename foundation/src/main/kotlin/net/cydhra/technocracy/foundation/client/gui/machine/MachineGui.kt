package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.tileentity.AbstractMachine
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

open class MachineGui(private val machine: AbstractMachine?, private val container: MachineContainer?, private val
playerInventory: InventoryPlayer,
                      private val tabs: Array<Tab>) :
        GuiContainer
        (container) {

    companion object {
        val guiTexture: ResourceLocation = ResourceLocation("technocracy.foundation", "textures/gui/machine.png")
    }

    private var tab: Int = 0

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1F, 1F, 1F, 1F)
        Minecraft.getMinecraft().textureManager.bindTexture(guiTexture)
        drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize)
        this.tabs[tab].draw(mouseX, mouseY, partialTicks)
    }

    override fun updateScreen() {
        this.tabs[tab].update()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}
