package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent

abstract class RedstoneModeControl(val posX: Int, val posY: Int) : TCComponent {

    val width = 16
    val height = 16
    var hovered = false

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        hovered = mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun update() {

    }
}