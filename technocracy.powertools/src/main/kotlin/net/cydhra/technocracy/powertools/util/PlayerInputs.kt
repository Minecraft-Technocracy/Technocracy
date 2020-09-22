package net.cydhra.technocracy.powertools.util

import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.powertools.network.ClientInputPacket
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MovementInput
import net.minecraftforge.client.event.InputUpdateEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*


object PlayerInputs {
    private val inputs = mutableMapOf<UUID, PlayerInput>()

    operator fun get(player: EntityPlayer): PlayerInput {
        return get(player.uniqueID)
    }

    operator fun get(uuid: UUID): PlayerInput {
        return inputs.getOrPut(uuid, { PlayerInput() })
    }

    operator fun set(player: EntityPlayer, value: PlayerInput) {
        inputs[player.uniqueID] = value
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun update(event: InputUpdateEvent) {
        val last = get(event.entityPlayer)
        val current = PlayerInput.fromMovement(event.movementInput)
        if (current != last) {
            inputs[event.entityPlayer.uniqueID] = current
            PacketHandler.sendToServer(ClientInputPacket(current))
        }
    }

    class PlayerInput {

        companion object {
            @SideOnly(Side.CLIENT)
            fun fromMovement(input: MovementInput): PlayerInput {
                return PlayerInput().apply {
                    moveStrafe = input.moveStrafe
                    moveForward = input.moveForward
                    forwardKeyDown = input.forwardKeyDown
                    backKeyDown = input.backKeyDown
                    leftKeyDown = input.leftKeyDown
                    rightKeyDown = input.rightKeyDown
                    jump = input.jump
                    sneak = input.sneak
                }
            }
        }

        var moveStrafe = 0f
        var moveForward = 0f
        var forwardKeyDown = false
        var backKeyDown = false
        var leftKeyDown = false
        var rightKeyDown = false
        var jump = false
        var sneak = false

        override fun equals(other: Any?): Boolean {
            if (other is PlayerInput) {
                return moveStrafe == other.moveStrafe &&
                        moveForward == other.moveForward &&
                        forwardKeyDown == other.forwardKeyDown &&
                        backKeyDown == other.backKeyDown &&
                        leftKeyDown == other.leftKeyDown &&
                        rightKeyDown == other.rightKeyDown &&
                        jump == other.jump &&
                        sneak == other.sneak
            }

            return super.equals(other)
        }

        override fun hashCode(): Int {
            var result = moveStrafe.hashCode()
            result = 31 * result + moveForward.hashCode()
            result = 31 * result + forwardKeyDown.hashCode()
            result = 31 * result + backKeyDown.hashCode()
            result = 31 * result + leftKeyDown.hashCode()
            result = 31 * result + rightKeyDown.hashCode()
            result = 31 * result + jump.hashCode()
            result = 31 * result + sneak.hashCode()
            return result
        }
    }

}