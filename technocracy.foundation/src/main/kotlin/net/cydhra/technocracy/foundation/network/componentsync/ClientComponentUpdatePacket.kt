package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ClientComponentUpdatePacket(var tag: NBTTagCompound = NBTTagCompound()) : IMessage, IMessageHandler<ClientComponentUpdatePacket, IMessage> {

    override fun fromBytes(buf: ByteBuf?) {
        tag = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeTag(buf, tag)
    }

    override fun onMessage(packet: ClientComponentUpdatePacket, ctx: MessageContext): IMessage? {
        val te = ctx.serverHandler.player.world.getTileEntity(BlockPos.fromLong(packet.tag.getLong("pos")))
        if (te is MultiblockTileEntityBase) {
            (te.multiblockController as BaseMultiBlock).getComponents().filter { it.first == packet.tag.getString("name") }.forEach { (_, component) ->
                component.deserializeNBT(tag.getCompoundTag("component"))
            }
        } else if (te is TCAggregatable) {
            te.getComponents().filter { it.first == packet.tag.getString("name") }.forEach { (_, component) ->
                component.deserializeNBT(tag.getCompoundTag("component"))
            }
        }
        return null
    }
}