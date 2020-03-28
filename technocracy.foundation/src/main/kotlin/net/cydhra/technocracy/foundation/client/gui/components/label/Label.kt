package net.cydhra.technocracy.foundation.client.gui.components.label

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.client.gui.FontRenderer

abstract class Label(override var posX: Int, override var posY: Int, var text: String, val fontRenderer: FontRenderer) : TCComponent() {

    override fun drawTooltip(mouseX: Int, mouseY: Int) {}

    override fun update() {}

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {}

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + fontRenderer.getStringWidth(text) && mouseY > posY && mouseY < posY + fontRenderer.FONT_HEIGHT
    }
}