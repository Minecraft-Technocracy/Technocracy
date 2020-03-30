package net.cydhra.technocracy.foundation.client.gui.components.button

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity


abstract class Button(override var posX: Int, override var posY: Int, override var width: Int, override var height: Int, var text: String, val fontRenderer: FontRenderer, componentId: Int, val clientClick: ((player: EntityPlayer, tileEntity: TileEntity?, button: Int) -> Unit)? = null) : TCComponent() {

    override fun drawTooltip(mouseX: Int, mouseY: Int) {}

    override fun update() {
    }

    init {
        this.componentId = componentId
    }

    /*override fun handleClientClick(player: EntityPlayer, mouseButton: Int) {
        if (mouseButton == 0)
            onClick(player, (player.openContainer as TCContainer).tileEntity, mouseButton)
    }*/

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        val player = Minecraft.getMinecraft().player
        clientClick?.invoke(player, (player.openContainer as TCContainer).tileEntity, mouseButton)
        //onClick(player, (player.openContainer as TCContainer).tileEntity, mouseButton)
        super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height
    }
}