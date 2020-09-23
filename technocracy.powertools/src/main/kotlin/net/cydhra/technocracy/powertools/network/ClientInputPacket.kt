package net.cydhra.technocracy.powertools.network

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.util.intFromBools
import net.cydhra.technocracy.powertools.util.PlayerInputs
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class ClientInputPacket(var input: PlayerInputs.PlayerInput = PlayerInputs.PlayerInput()) : IMessage, IMessageHandler<ClientInputPacket, IMessage> {
    override fun fromBytes(buf: ByteBuf) {
        input.moveStrafe = buf.readFloat()
        input.moveForward = buf.readFloat()

        val i = buf.readInt()

        input.sneak = i[0]
        input.jump = i[1]
        input.leftKeyDown = i[2]
        input.rightKeyDown = i[3]
        input.forwardKeyDown = i[4]
        input.backKeyDown = i[5]
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeFloat(input.moveStrafe)
        buf.writeFloat(input.moveForward)

        with(input) {
            buf.writeInt(intFromBools(sneak, jump,
                    leftKeyDown, rightKeyDown,
                    forwardKeyDown, backKeyDown))
        }
    }

    private operator fun Int.get(index: Int): Boolean {
        if (index > 32) throw ArrayIndexOutOfBoundsException("index to long")
        return (this and (1 shl index)) != 0
    }

    override fun onMessage(message: ClientInputPacket, ctx: MessageContext): IMessage? {
        val player = ctx.serverHandler.player
        val movementInput = message.input
        PlayerInputs[player] = movementInput
        return null
    }
}