package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.model.items.util.IItemScrollEvent
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ItemScrollPacket(var direction: Int = 0) : IMessage, IMessageHandler<ItemScrollPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
        direction = buf.readByte().toInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeByte(direction)
    }

    override fun onMessage(message: ItemScrollPacket, ctx: MessageContext): IMessage? {
        val player = ctx.serverHandler.player
        val stack = player.heldItemMainhand
        if (stack.item is IItemScrollEvent) (stack.item as IItemScrollEvent).mouseScroll(player, stack, message.direction)
        return null
    }
}