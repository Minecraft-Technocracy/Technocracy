package net.cydhra.technocracy.foundation.client.gui.components.energymeter

import net.cydhra.technocracy.foundation.client.gui.components.TCCapabilityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent

abstract class EnergyMeter(override var posX: Int, override var posY: Int, component: TileEntityEnergyStorageComponent) : TCCapabilityComponent<TileEntityEnergyStorageComponent>(component) {

    /**
     * energy level from 0.0 to 1.0
     */
    var level = 0.0
    var lastLevel = -1.0

    override var width = 10
    override var height = 50

    override fun update() {
        lastLevel = level
        this.level += 0.01
        if (level > 1.1)
            this.level = 0.0

    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {

    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }
}