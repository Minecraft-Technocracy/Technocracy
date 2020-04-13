package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientRequestSyncPacket: IMessage, IMessageHandler<ClientRequestSyncPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf?) {
    }

    override fun toBytes(buf: ByteBuf?) {
    }

    override fun onMessage(packet: ClientRequestSyncPacket, ctx: MessageContext): IMessage? {
        val container = ctx.serverHandler.player.openContainer

        if (container !is TCContainer)
            return null

        return MachineInfoPacket(container.provider)
    }
}