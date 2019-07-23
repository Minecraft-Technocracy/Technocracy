package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MachineInfoResponse(var compound: NBTTagCompound = NBTTagCompound()) : IMessage, IMessageHandler<MachineInfoResponse, IMessage> {

    override fun fromBytes(buf: ByteBuf?) {
        compound = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeTag(buf, compound)
    }

    override fun onMessage(packet: MachineInfoResponse, context: MessageContext): IMessage? {
        val te = Minecraft.getMinecraft().world.getTileEntity((BlockPos.fromLong(packet.compound.getLong("pos"))))
        if (te is MachineTileEntity) {
            te.getComponents().forEach { (name, component) ->
                val tag = packet.compound.getCompoundTag(name)
                component.deserializeNBT(tag)
            }
        } else if(te is TileEntityMultiBlockPart<*>) {
            (te.multiblockController as BaseMultiBlock).getComponents().forEach { (name, component) ->
                val tag = packet.compound.getCompoundTag(name)
                component.deserializeNBT(tag)
            }
        }
        return null
    }

}
