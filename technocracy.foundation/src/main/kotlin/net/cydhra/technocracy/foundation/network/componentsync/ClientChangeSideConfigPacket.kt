package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractDirectionalCapabilityTileEntityComponent
import net.cydhra.technocracy.foundation.model.components.IAggregatable
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientChangeSideConfigPacket() : IMessage, IMessageHandler<ClientChangeSideConfigPacket, IMessage> {
    var componentName: String = ""
    var facing: EnumFacing = EnumFacing.NORTH
    var addFace = true

    constructor(componentName: String, facing: EnumFacing, addFace: Boolean) : this() {
        this.componentName = componentName
        this.facing = facing
        this.addFace = addFace
    }

    override fun fromBytes(buf: ByteBuf) {
        componentName = ByteBufUtils.readUTF8String(buf)
        facing = EnumFacing.values()[buf.readInt()]
        addFace = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, componentName)
        buf.writeInt(facing.ordinal)
        buf.writeBoolean(addFace)
    }

    override fun onMessage(packet: ClientChangeSideConfigPacket, context: MessageContext): IMessage? {
        val container = context.serverHandler.player.openContainer

        if (container !is TCContainer)
            return null

        val te = container.tileEntity

        val components = if (te is IAggregatable) {
            te.getComponents()
        } else if (te is TileEntityMultiBlockPart<*>) {
            (te.multiblockController as BaseMultiBlock).getComponents()
        } else return null

        components.filter { it.second is AbstractDirectionalCapabilityTileEntityComponent && it.first == packet.componentName }.forEach { (_, component) ->
            val comp = (component as AbstractDirectionalCapabilityTileEntityComponent)
            if (packet.addFace) {
                comp.facing.add(packet.facing)
            } else {
                comp.facing.remove(packet.facing)
            }
            comp.markDirty(true)
        }

        return null
    }

}
