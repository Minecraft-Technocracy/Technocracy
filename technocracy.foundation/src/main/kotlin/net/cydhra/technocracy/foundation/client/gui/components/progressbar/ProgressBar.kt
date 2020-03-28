package net.cydhra.technocracy.foundation.client.gui.components.progressbar

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.util.math.MathHelper

enum class Orientation() {
    UP, DOWN, LEFT, RIGHT
}

abstract class ProgressBar(override var posX: Int, override var posY: Int, val orientation: Orientation = Orientation.RIGHT) :
        TCComponent() {
    var progress = 0.0
    var lastprogress = 0.0

    override var width = 22
    override var height = 15

    override fun update() {
        this.lastprogress = progress
        this.progress += 0.01
        this.progress = MathHelper.clamp(this.progress, 0.0, 1.0)

        if (progress == 1.0) {
            this.progress = 0.0
        }
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {

    }
}