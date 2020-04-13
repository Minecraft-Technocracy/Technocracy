package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MachineInfoPacket() : IMessage, IMessageHandler<MachineInfoPacket, IMessage> {
    var tag: NBTTagCompound = NBTTagCompound()

    constructor(te: IAggregatable?) : this() {
        if (te is TileEntityMultiBlockPart<*>) {
            if (te.multiblockController != null)
                tag = getTagForMachine((te.multiblockController as BaseMultiBlock).getComponents())
        } else if (te is IAggregatable) {
            tag = getTagForMachine(te.getComponents())
        }
    }

    constructor(components: MutableList<Pair<String, IComponent>>) : this() {
        tag = getTagForMachine(components)
    }

    constructor(tag: NBTTagCompound) : this() {
        this.tag = tag
    }

    fun getTagForMachine(components: List<Pair<String, IComponent>>): NBTTagCompound {
        val tag = NBTTagCompound()
        components.forEach { (name, component) ->
            tag.setTag(name, component.serializeNBT())
        }
        return tag
    }

    override fun fromBytes(buf: ByteBuf) {
        tag = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeTag(buf, tag)
    }

    override fun onMessage(packet: MachineInfoPacket, context: MessageContext): IMessage? {

        val container = if (context.side.isClient) Minecraft.getMinecraft().player.openContainer else context.serverHandler.player.openContainer

        if (container !is TCContainer)
            return null

        //todo send update packet to clients that have open the same gui
        val te = container.provider
        //val te = Minecraft.getMinecraft().world.getTileEntity((BlockPos.fromLong(packet.tag.getLong("pos"))))
        if (te is TileEntityMultiBlockPart<*>) {
            (te.multiblockController as BaseMultiBlock).getComponents().forEach { (name, component) ->
                val tag = packet.tag.getCompoundTag(name)
                component.deserializeNBT(tag)
            }
        } else {
            te.getComponents().forEach { (name, component) ->
                val tag = packet.tag.getCompoundTag(name)
                component.deserializeNBT(tag)
            }
        }
        return null
    }

}
