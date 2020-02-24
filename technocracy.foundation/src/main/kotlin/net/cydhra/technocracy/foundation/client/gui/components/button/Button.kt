package net.cydhra.technocracy.foundation.client.gui.components.button

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.client.gui.FontRenderer


abstract class Button(val posX: Int, val posY: Int, val width: Int, val height: Int, val text: String, val fontRenderer: FontRenderer) : TCComponent {

    override fun drawTooltip(mouseX: Int, mouseY: Int) {}

    override fun update() {}

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {}

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height
    }
}