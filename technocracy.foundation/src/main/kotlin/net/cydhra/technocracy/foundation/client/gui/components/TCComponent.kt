package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.network.ComponentClickPacket
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.minecraft.entity.player.EntityPlayer
import kotlin.properties.Delegates

abstract class TCComponent : ITCComponent {
    var componentId by Delegates.notNull<Int>()
    open fun handleClientClick(player: EntityPlayer, mouseButton: Int) {}

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        PacketHandler.sendToServer(ComponentClickPacket(componentId, mouseButton))
    }
}

interface ITCComponent {

    fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float)
    fun drawTooltip(mouseX: Int, mouseY: Int)

    fun update()

    fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int)
    fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean

}
