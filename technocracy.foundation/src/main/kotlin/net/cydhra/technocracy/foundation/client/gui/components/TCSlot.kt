package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

class TCSlot(inventoryIn: IInventory, index: Int, xPosition: Int, yPosition: Int) : Slot(inventoryIn, index,
        xPosition, yPosition), TCComponent {

    override fun update() {
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.color(1F, 1F, 1F, 1F)
        GuiContainer.drawModalRectWithCustomSizedTexture(xPos - 1, yPos - 1, 0F, 10F, 18, 18, 256F,
                256F)
    }

    private var enabled: Boolean = true

    override fun isEnabled(): Boolean {
        return this.enabled
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
