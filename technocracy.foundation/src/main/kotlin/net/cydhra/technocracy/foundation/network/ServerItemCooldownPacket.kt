package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ServerItemCooldownPacket(var entityId: Int = 0, var item: Item = Items.AIR, var ticks: Int = 0) : IMessage, IMessageHandler<ServerItemCooldownPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
        this.entityId = buf.readInt()
        this.item = Item.getItemById(buf.readInt())
        this.ticks = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(entityId)
        buf.writeInt(Item.getIdFromItem(item))
        buf.writeInt(ticks)
    }

    override fun onMessage(message: ServerItemCooldownPacket, ctx: MessageContext): IMessage? {
        val player = Minecraft.getMinecraft().world.playerEntities.find { it.entityId == message.entityId }
        player?.cooldownTracker?.setCooldown(message.item, message.ticks)
        return null
    }
}