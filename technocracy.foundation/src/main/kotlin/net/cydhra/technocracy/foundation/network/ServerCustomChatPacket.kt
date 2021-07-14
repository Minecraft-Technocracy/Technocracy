package net.cydhra.technocracy.foundation.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.util.readChatComponent
import net.cydhra.technocracy.foundation.util.writeChatComponent
import net.minecraft.client.Minecraft
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ServerCustomChatPacket(var msg: ITextComponent = TextComponentString(""), var id: Int = 546879123) : IMessage,
    IMessageHandler<ServerCustomChatPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
        msg = buf.readChatComponent()
        id = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeChatComponent(msg)
        buf.writeInt(id)
    }

    override fun onMessage(message: ServerCustomChatPacket, ctx: MessageContext): IMessage? {
        val chat = Minecraft.getMinecraft().ingameGUI.chatGUI

        if (message.msg.formattedText.isBlank()) {
            chat.deleteChatLine(message.id)
        } else {
            chat.printChatMessageWithOptionalDeletion(message.msg, message.id)
        }

        return null
    }
}