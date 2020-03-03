package net.cydhra.technocracy.foundation.client.gui.components.button

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.player.EntityPlayer
import java.util.function.Consumer


abstract class Button(val posX: Int, val posY: Int, val width: Int, val height: Int, val text: String, val fontRenderer: FontRenderer, val onClick: Consumer<EntityPlayer>) : TCComponent() {

    override fun drawTooltip(mouseX: Int, mouseY: Int) {}

    override fun update() {}

    override fun handleClientClick(player: EntityPlayer, mouseButton: Int) {
        if (mouseButton == 0)
            onClick.accept(player)
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            onClick.accept(Minecraft.getMinecraft().player)
        }
        super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height
    }
}