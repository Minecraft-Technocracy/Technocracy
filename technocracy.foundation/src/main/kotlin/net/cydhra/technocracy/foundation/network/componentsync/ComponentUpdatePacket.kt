package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ComponentUpdatePacket(var tag: NBTTagCompound = NBTTagCompound()) : IMessage, IMessageHandler<ComponentUpdatePacket, IMessage> {
    override fun fromBytes(buf: ByteBuf?) {
        tag = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeTag(buf, tag)
    }

    override fun onMessage(packet: ComponentUpdatePacket, ctx: MessageContext): IMessage? {
        val container = if (ctx.side.isClient) Minecraft.getMinecraft().player.openContainer else ctx.serverHandler.player.openContainer

        if (container !is TCContainer)
            return null

        //todo send update packet to clients that have open the same gui
        val te = container.tileEntity
        if (te is MultiblockTileEntityBase) {
            (te.multiblockController as BaseMultiBlock).getComponents().filter { it.first == packet.tag.getString("name") }.forEach { (_, component) ->
                component.deserializeNBT(packet.tag.getCompoundTag("component"))
            }
            te.markDirty()
        } else if (te is TCAggregatable) {
            te.getComponents().filter { it.first == packet.tag.getString("name") }.forEach { (_, component) ->
                component.deserializeNBT(packet.tag.getCompoundTag("component"))
            }
            te.markDirty()
        }

        return null
    }
}