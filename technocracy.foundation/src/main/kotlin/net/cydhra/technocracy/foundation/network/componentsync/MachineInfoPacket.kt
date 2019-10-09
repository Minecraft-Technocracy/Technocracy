package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MachineInfoPacket(var tag: NBTTagCompound = NBTTagCompound()) : IMessage, IMessageHandler<MachineInfoPacket, IMessage> {

    override fun fromBytes(buf: ByteBuf) {
        tag = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeTag(buf, tag)
    }

    override fun onMessage(packet: MachineInfoPacket, context: MessageContext): IMessage? {
        val te = Minecraft.getMinecraft().world.getTileEntity((BlockPos.fromLong(packet.tag.getLong("pos"))))
        if (te is MachineTileEntity) {
            te.getComponents().forEach { (name, component) ->
                val tag = packet.tag.getCompoundTag(name)
                component.deserializeNBT(tag)
            }
        } else if (te is TileEntityMultiBlockPart<*>) {
            (te.multiblockController as BaseMultiBlock).getComponents().forEach { (name, component) ->
                val tag = packet.tag.getCompoundTag(name)
                component.deserializeNBT(tag)
            }
        }
        return null
    }

}
