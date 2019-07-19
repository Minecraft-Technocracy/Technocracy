package net.cydhra.technocracy.foundation.network

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.relauncher.Side


object PacketHandler {
    private val simpleHandler = NetworkRegistry.INSTANCE.newSimpleChannel(TCFoundation.MODID)
    private var amount = 0

    /**
     * Register a message and it's associated handler. The message handler will
     * be registered on the supplied side (this is the side where you want the message to be processed and acted upon).
     *
     * @param messageHandler the message handler type
     * @param requestMessageType the message type
     * @param side the side for the handler
     */
    fun <REQ : IMessage, REPLY : IMessage> registerPacket(messageHandler: Class<out IMessageHandler<REQ, REPLY>>, requestMessageType: Class<REQ>, side: Side) {
        simpleHandler.registerMessage(messageHandler, requestMessageType, amount++, side)
    }

    fun sendToServer(message: IMessage) {
        simpleHandler.sendToServer(message)
    }

    /**
     * Sends Message to all players
     */
    fun sendToClient(message: IMessage, player: EntityPlayerMP) {
        simpleHandler.sendTo(message, player)
    }

    /**
     * Sends Message to all players
     */
    fun sendToAll(message: IMessage) {
        simpleHandler.sendToAll(message)
    }
}