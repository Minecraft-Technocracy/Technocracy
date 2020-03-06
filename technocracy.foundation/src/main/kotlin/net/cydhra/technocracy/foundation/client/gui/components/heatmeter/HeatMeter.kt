package net.cydhra.technocracy.foundation.client.gui.components.heatmeter

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent


abstract class HeatMeter(val posX: Int, val posY: Int) : TCComponent() {
    /**
     * heat level from 0.0 to 1.0
     */
    var level = 0.0
    var lastLevel = -1.0

    override var width = 10
    override var height = 50

    override fun update() {
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {

    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }
}