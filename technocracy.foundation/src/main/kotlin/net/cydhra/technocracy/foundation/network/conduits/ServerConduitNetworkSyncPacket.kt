package net.cydhra.technocracy.foundation.network.conduits

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.conduits.ConduitNetwork
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Synchronized a conduit network chunk to the client, when the client starts watching it.
 */
class ServerConduitNetworkSyncPacket() : IMessage, IMessageHandler<ServerConduitNetworkSyncPacket, IMessage> {
    private var dimension: Int = 0
    private lateinit var data: NBTTagCompound
    private lateinit var chunkPos: ChunkPos

    constructor(dimension: Int, nbtTagCompound: NBTTagCompound, chunkPos: ChunkPos) : this() {
        this.dimension = dimension
        this.data = nbtTagCompound
        this.chunkPos = chunkPos
    }

    override fun fromBytes(buf: ByteBuf) {
        this.dimension = buf.readInt()
        this.chunkPos = ChunkPos(buf.readInt(), buf.readInt())
        this.data = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(this.dimension)
        buf.writeInt(this.chunkPos.x)
        buf.writeInt(this.chunkPos.z)
        ByteBufUtils.writeTag(buf, this.data)
    }

    override fun onMessage(message: ServerConduitNetworkSyncPacket, ctx: MessageContext): IMessage? {
        ConduitNetwork.receiveNetworkChunk(message.dimension, message.chunkPos, message.data)
        return null
    }

}