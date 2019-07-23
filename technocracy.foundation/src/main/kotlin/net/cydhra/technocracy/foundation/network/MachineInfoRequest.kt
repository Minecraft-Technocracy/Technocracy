package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MachineInfoRequest(var dim: Int = 0, var pos: BlockPos = BlockPos.ORIGIN) : IMessage, IMessageHandler<MachineInfoRequest, IMessage> {

    override fun fromBytes(buf: ByteBuf) {
        dim = buf.readInt()
        pos = BlockPos.fromLong(buf.readLong())
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(dim)
        buf.writeLong(pos.toLong())
    }

    override fun onMessage(packet: MachineInfoRequest, context: MessageContext): IMessage? {
        val world = DimensionManager.getWorld(packet.dim)
        if (world.isBlockLoaded(packet.pos)) {
            val te = world.getTileEntity(packet.pos)

            if (te is MachineTileEntity) {
                val tag = NBTTagCompound()
                te.getComponents().forEach { (name, component) ->
                    tag.setTag(name, component.serializeNBT())
                }
                tag.setLong("pos", packet.pos.toLong())
                PacketHandler.sendToClient(MachineInfoResponse(tag), context.serverHandler.player)
            }
        }
        return null
    }

}
