package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.network.ComponentClickPacket
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import kotlin.properties.Delegates

abstract class TCComponent : ITCComponent {
    var componentId by Delegates.notNull<Int>()
    open fun handleClientClick(player: EntityPlayer, mouseButton: Int) {}

    fun setSize(width: Int = this.width, height: Int = this.height): TCComponent {
        this.width = width
        this.height = height
        return this
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        PacketHandler.sendToServer(ComponentClickPacket(componentId, mouseButton))
    }

    fun drawModalRectWithCustomSizedTexture(x: Double, y: Double, u: Double, v: Double, width: Double, height: Double, textureWidth: Float, textureHeight: Float) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(x, (y + height), 0.0).tex((u * f), ((v + height.toFloat()) * f1)).endVertex()
        bufferbuilder.pos((x + width), (y + height), 0.0).tex(((u + width.toFloat()) * f), ((v + height.toFloat()) * f1)).endVertex()
        bufferbuilder.pos((x + width), y, 0.0).tex(((u + width.toFloat()) * f), (v * f1)).endVertex()
        bufferbuilder.pos(x, y, 0.0).tex((u * f), (v * f1)).endVertex()
        tessellator.draw()
    }
}

interface ITCComponent {
    var width: Int
    var height: Int
    var posX: Int
    var posY: Int

    fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float)
    fun drawTooltip(mouseX: Int, mouseY: Int)

    fun update()

    fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int)
    fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean

}
