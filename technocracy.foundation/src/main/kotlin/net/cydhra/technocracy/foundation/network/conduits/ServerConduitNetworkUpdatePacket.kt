package net.cydhra.technocracy.foundation.network.conduits

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.conduits.ConduitNetwork
import net.cydhra.technocracy.foundation.conduits.parts.Part
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Inform the client of updates in the conduit network. The packet potentially bundles multiple updates together, to
 * minimize bandwidth.
 */
class ServerConduitNetworkUpdatePacket() : IMessage, IMessageHandler<ServerConduitNetworkUpdatePacket, IMessage> {
    private var dimension: Int = 0
    private lateinit var partsAdded: List<Pair<BlockPos, Part>>
    private lateinit var partsRemoved: List<Pair<BlockPos, Part>>

    constructor(
        dimension: Int,
        partsAdded: List<Pair<BlockPos, Part>>,
        partsRemoved: List<Pair<BlockPos, Part>>
    ) : this() {
        this.dimension = dimension
        this.partsAdded = partsAdded
        this.partsRemoved = partsRemoved
    }

    override fun fromBytes(buf: ByteBuf) {
        this.dimension = buf.readInt()

        val added = mutableListOf<Pair<BlockPos, Part>>()
        val partsAddedNum = buf.readByte()
        for (i in 0 until partsAddedNum) {
            val pos = BlockPos.fromLong(buf.readLong())
            val part = Part.readFromByteBuf(buf)
            added.add(pos to part)
        }

        val removed = mutableListOf<Pair<BlockPos, Part>>()
        val partsRemovedNum = buf.readByte()
        for (i in 0 until partsRemovedNum) {
            val pos = BlockPos.fromLong(buf.readLong())
            val part = Part.readFromByteBuf(buf)
            removed.add(pos to part)
        }

        this.partsAdded = added
        this.partsRemoved = removed
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(this.dimension)

        buf.writeByte(partsAdded.size)
        for ((pos, part) in partsAdded) {
            buf.writeLong(pos.toLong())
            part.writeToByteBuf(buf)
        }

        buf.writeInt(partsRemoved.size)
        for ((pos, part) in partsRemoved) {
            buf.writeLong(pos.toLong())
            part.writeToByteBuf(buf)
        }
    }

    override fun onMessage(message: ServerConduitNetworkUpdatePacket, ctx: MessageContext): IMessage? {
        ConduitNetwork.receiveNetworkUpdates(message.dimension, message.partsAdded, message.partsRemoved)
        return null
    }

}