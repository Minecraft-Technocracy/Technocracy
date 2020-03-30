package net.cydhra.technocracy.foundation.client.gui.components.slot


import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.container.components.PlayerSlotComponent
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

/**
 * A gui slot that is used to represent player inventory slots.
 */
class TCSlotPlayer(inventoryIn: IInventory, override val index: Int, xPosition: Int, yPosition: Int, val gui: TCGui) :
        Slot(inventoryIn, index,
                xPosition, yPosition), ITCSlot {

    lateinit var containerSlot: PlayerSlotComponent

    init {
        for (slot in gui.container.inventorySlots) {
            if (slot is PlayerSlotComponent && slot.inventory == inventory && slot.slotIndex == index) {
                slot.xPos = xPosition
                slot.yPos = yPosition
                containerSlot = slot
            }
        }
    }

    override var posX: Int
        get() = super.xPos
        set(value) {
            super.xPos = value
        }
    override var posY: Int
        get() = super.yPos
        set(value) {
            super.yPos = value
        }

    override var width = 18
    override var height = 18

    override var type: DynamicInventoryCapability.InventoryType = DynamicInventoryCapability.InventoryType.BOTH

    override fun update() {
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.color(1F, 1F, 1F, 1F)
        GuiContainer.drawModalRectWithCustomSizedTexture(xPos - 1 + x, yPos - 1 + y, 0F, 10F, width, height, 256F,
                256F)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val stack = inventory.getStackInSlot(slotIndex)
        if (stack.item != Items.AIR)
            gui.renderHoveredItemToolTip(mouseX, mouseY)
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        // handled elsewhere
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > xPos && mouseX < xPos + width && mouseY > yPos && mouseY < yPos + height
    }

    override fun setEnabled(enabled: Boolean) {
        containerSlot.enabled = enabled
    }

    /**
     * This method overrides [net.minecraft.inventory.Slot.isEnabled] and allows that to be ignored by our own value.
     */
    override fun isEnabled(): Boolean {
        return containerSlot.enabled
    }

    override val isPlayerInventory = true
}
