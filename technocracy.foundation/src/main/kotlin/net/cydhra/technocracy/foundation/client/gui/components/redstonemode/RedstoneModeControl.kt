package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent

abstract class RedstoneModeControl(val posX: Int, val posY: Int) : TCComponent() {

    override var width = 16
    override var height = 16
    var hovered = false

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        hovered = mouseX > posX + x && mouseX < posX + width + x && mouseY > posY + y && mouseY < posY + height + y
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun update() {

    }
}