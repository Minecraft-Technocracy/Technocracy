package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.util.player
import net.cydhra.technocracy.foundation.util.syncToMainThread
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientComponentClickPacket(var componentId: Int = -1, var clickType: Int = -1) : IMessage, IMessageHandler<ClientComponentClickPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf?) {
        componentId = buf?.readInt()!!
        clickType = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf?) {
        buf?.writeInt(componentId)
        buf?.writeInt(clickType)
    }

    override fun onMessage(message: ClientComponentClickPacket, ctx: MessageContext): IMessage? {
        return ctx.syncToMainThread {
            val container = player.openContainer
            if (container !is TCContainer)
                return@syncToMainThread null
            container.clickComponent(ctx.serverHandler.player, message.componentId, message.clickType)
            null
        }
    }
}