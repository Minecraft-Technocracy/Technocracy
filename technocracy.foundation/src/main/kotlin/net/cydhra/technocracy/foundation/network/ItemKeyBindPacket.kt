package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.model.items.util.IItemKeyBindEvent
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ItemKeyBindPacket : IMessage, IMessageHandler<ItemKeyBindPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
    }

    override fun toBytes(buf: ByteBuf) {
    }

    override fun onMessage(message: ItemKeyBindPacket, ctx: MessageContext): IMessage? {
        val player = ctx.serverHandler.player
        val stack = player.heldItemMainhand
        if (stack.item is IItemKeyBindEvent) (stack.item as IItemKeyBindEvent).keyPress(player, stack)
        return null
    }
}