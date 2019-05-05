package net.cydhra.technocracy.foundation.client.gui.components

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

class TCSlot(inventoryIn: IInventory, index: Int, xPosition: Int, yPosition: Int) : Slot(inventoryIn, index,
        xPosition, yPosition), TCComponent {

    override fun update() {
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
    }

    private var enabled: Boolean = true

    override fun isEnabled(): Boolean {
        return this.enabled
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
