package net.cydhra.technocracy.foundation.client.events

import net.cydhra.technocracy.foundation.items.general.IItemKeyBindEvent
import net.cydhra.technocracy.foundation.items.general.IItemScrollEvent
import net.cydhra.technocracy.foundation.network.ItemKeyBindPacket
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.ItemScrollPacket
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent

object KeyEventHandler {

    @SubscribeEvent
    fun handleMouseInput(event: MouseEvent) {
        if (event.dwheel == 0) return
        val player = Minecraft.getMinecraft().player
        val stack = player.heldItemMainhand
        if (!stack.isEmpty && stack.item is IItemScrollEvent && player.isSneaking) {
            val scrollDir = if (event.dwheel < 0) -1 else 1

            if (player.world.isRemote) {
                PacketHandler.sendToServer(ItemScrollPacket(scrollDir))
            } else {
                (stack.item as IItemScrollEvent).mouseScroll(player, stack, scrollDir)
            }

            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun handleKeyInput(event: InputEvent.KeyInputEvent) {
        val player = Minecraft.getMinecraft().player
        val stack = player.heldItemMainhand
        if (!stack.isEmpty && stack.item is IItemKeyBindEvent && Minecraft.getMinecraft().inGameHasFocus) {

            if (player.world.isRemote) {
                PacketHandler.sendToServer(ItemKeyBindPacket())
            } else {
                (stack.item as IItemKeyBindEvent).keyPress(player, stack)
            }

            event.isCanceled = true
        }
    }
}