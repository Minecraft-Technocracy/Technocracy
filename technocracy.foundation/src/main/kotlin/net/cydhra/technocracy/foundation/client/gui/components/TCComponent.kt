package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.network.ClientComponentClickPacket
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side

abstract class TCComponent : ITCComponent {
    override var componentId = -1

    fun setSize(width: Int = this.width, height: Int = this.height): TCComponent {
        this.width = width
        this.height = height
        return this
    }

    fun setId(id: Int) {
        componentId = id
    }

    override var onClick: ((side: Side, player: EntityPlayer, tileEntity: IAggregatable?, button: Int) -> Unit)? = null

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        onClick?.let {
            it(Side.CLIENT, Minecraft.getMinecraft().player, gui.container.provider, mouseButton)
            PacketHandler.sendToServer(ClientComponentClickPacket(componentId, mouseButton))
        }
    }

    override fun onMouseScroll(dir: Int) {
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

    var componentId: Int
    var gui: TCGui

    fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float)
    fun drawTooltip(mouseX: Int, mouseY: Int)

    fun update()

    var onClick: ((side: Side, player: EntityPlayer, tileEntity: IAggregatable?, button: Int) -> Unit)?

    //fun handleClientClick(player: EntityPlayer?, tile: TileEntity?, mouseButton: Int) {}

    fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int)
    fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean

    fun onMouseScroll(dir: Int)

}
