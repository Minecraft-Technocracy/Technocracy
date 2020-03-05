package net.cydhra.technocracy.foundation.client.gui.components.progressbar

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.util.math.MathHelper

enum class Orientation() {
    UP, DOWN, LEFT, RIGHT
}

abstract class ProgressBar(val posX: Int, val posY: Int, val orientation: Orientation = Orientation.RIGHT) :
        TCComponent() {
    var progress = 0F

    override var width = 22
    override var height = 15

    override fun update() {
        this.progress += 0.01F
        this.progress = MathHelper.clamp(this.progress, 0F, 1F)

        if (progress == 1.0F) {
            this.progress = 0F
        }
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {

    }
}