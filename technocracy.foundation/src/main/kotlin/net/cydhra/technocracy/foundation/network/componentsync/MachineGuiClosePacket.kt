package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MachineGuiClosePacket : IMessage, IMessageHandler<MachineGuiClosePacket, IMessage> {

    override fun fromBytes(buf: ByteBuf) {}

    override fun toBytes(buf: ByteBuf) {}

    override fun onMessage(packet: MachineGuiClosePacket, context: MessageContext): IMessage? {
        guiInfoPacketSubscribers.remove(context.serverHandler.player)
        return null
    }

}