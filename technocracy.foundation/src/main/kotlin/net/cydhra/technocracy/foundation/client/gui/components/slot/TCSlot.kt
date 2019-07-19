package net.cydhra.technocracy.foundation.client.gui.components.slot

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

open class TCSlot(inventoryIn: IInventory, index: Int, xPosition: Int, yPosition: Int, val gui: TCGui) : Slot(inventoryIn, index,
        xPosition, yPosition), TCComponent {

    override fun update() {
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.color(1F, 1F, 1F, 1F)
        GuiContainer.drawModalRectWithCustomSizedTexture(xPos - 1, yPos - 1, 0F, 10F, 18, 18, 256F,
                256F)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val stack = inventory.getStackInSlot(slotNumber)
        if(stack.item != Items.AIR)
            gui.renderHoveredItemToolTip(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        // handled elsewhere
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > xPos && mouseX < xPos + 18 && mouseY > yPos && mouseY < yPos + 18
    }

    private var enabled: Boolean = true

    override fun isEnabled(): Boolean {
        return this.enabled
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
