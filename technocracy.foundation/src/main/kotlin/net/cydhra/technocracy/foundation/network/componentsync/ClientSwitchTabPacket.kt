package net.cydhra.technocracy.foundation.network.componentsync

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientSwitchTabPacket(var tabId: Int = -1) : IMessage, IMessageHandler<ClientSwitchTabPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf?) {
        tabId = buf!!.readInt()
    }

    override fun toBytes(buf: ByteBuf?) {
        buf!!.writeInt(tabId)
    }

    override fun onMessage(packet: ClientSwitchTabPacket, ctx: MessageContext): IMessage? {
        val container = ctx.serverHandler.player.openContainer

        if (container !is TCContainer)
            return null

        container.tabs.clear()
        container.inventorySlots.clear()
        container.inventoryItemStacks.clear()

        (container.provider as TCTileEntityGuiProvider).getGui(
            ctx.serverHandler.player,
            SimpleGui(container = container)
        )

        container.activeTab = packet.tabId

        return null
    }
}