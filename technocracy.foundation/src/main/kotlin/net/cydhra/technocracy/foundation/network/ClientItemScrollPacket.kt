package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.content.items.util.IItemScrollEvent
import net.cydhra.technocracy.foundation.util.player
import net.cydhra.technocracy.foundation.util.syncToMainThread
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientItemScrollPacket(var direction: Int = 0) : IMessage, IMessageHandler<ClientItemScrollPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
        direction = buf.readByte().toInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeByte(direction)
    }

    override fun onMessage(message: ClientItemScrollPacket, ctx: MessageContext): IMessage? {
        ctx.syncToMainThread {
            val player = player
            val stack = player.heldItemMainhand
            if (stack.item is IItemScrollEvent) (stack.item as IItemScrollEvent).mouseScroll(player, stack, message.direction)
            null
        }
        return null
    }
}