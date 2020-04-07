package net.cydhra.technocracy.foundation.client.gui.components.slot


import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side

/**
 * A gui slot that is used to represent player inventory slots.
 */
class TCSlotPlayer(inventoryIn: IInventory, override val index: Int, xPosition: Int, yPosition: Int, override var gui: TCGui) :
        Slot(inventoryIn, index,
                xPosition, yPosition), ITCSlot {

    override var onClick: ((side: Side, player: EntityPlayer, tileEntity: TileEntity?, button: Int) -> Unit)? = null

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

    override var componentId = 0

    override var width = 18
    override var height = 18

    override var type: DynamicInventoryCapability.InventoryType = DynamicInventoryCapability.InventoryType.BOTH

    override fun update() {
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCClientGuiImpl.guiComponents)
        GlStateManager.color(1F, 1F, 1F, 1F)
        GuiContainer.drawModalRectWithCustomSizedTexture(xPos - 1 + x, yPos - 1 + y, 0F, 10F, width, height, 256F,
                256F)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val stack = inventory.getStackInSlot(slotIndex)
        if (stack.item != Items.AIR)
            (gui as TCClientGuiImpl).renderHoveredItemToolTip(mouseX, mouseY)
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        // handled elsewhere
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > xPos && mouseX < xPos + width && mouseY > yPos && mouseY < yPos + height
    }

    override var internal_enabled = true

    /**
     * This method overrides [net.minecraft.inventory.Slot.isEnabled] and allows that to be ignored by our own value.
     */
    override fun isEnabled(): Boolean {
        return internal_enabled
    }

    override val isPlayerInventory = true
}
