package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.content.items.util.IItemKeyBindEvent
import net.cydhra.technocracy.foundation.util.player
import net.cydhra.technocracy.foundation.util.syncToMainThread
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientItemKeyBindPacket : IMessage, IMessageHandler<ClientItemKeyBindPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
    }

    override fun toBytes(buf: ByteBuf) {
    }

    override fun onMessage(message: ClientItemKeyBindPacket, ctx: MessageContext): IMessage? {
        ctx.syncToMainThread {
            val player = player
            val stack = player.heldItemMainhand
            if (stack.item is IItemKeyBindEvent) (stack.item as IItemKeyBindEvent).keyPress(player, stack)
            null
        }

        return null
    }
}