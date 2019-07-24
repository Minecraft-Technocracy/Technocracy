package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MachineGuiOpenPacket(var pos: BlockPos = BlockPos.ORIGIN, var dim: Int = -1) : IMessage, IMessageHandler<MachineGuiOpenPacket, IMessage> {

    override fun fromBytes(buf: ByteBuf) {
        dim = buf.readInt()
        pos = BlockPos.fromLong(buf.readLong())
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(dim)
        buf.writeLong(pos.toLong())
    }

    override fun onMessage(packet: MachineGuiOpenPacket, context: MessageContext): IMessage? {
        guiInfoPacketSubscribers[context.serverHandler.player] = Pair(packet.pos, packet.dim)
        return null
    }

}
