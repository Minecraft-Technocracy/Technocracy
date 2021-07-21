package net.cydhra.technocracy.foundation.client.events

import net.cydhra.technocracy.foundation.content.items.util.IItemKeyBindEvent
import net.cydhra.technocracy.foundation.content.items.util.IItemScrollEvent
import net.cydhra.technocracy.foundation.network.ClientItemKeyBindPacket
import net.cydhra.technocracy.foundation.network.ClientItemScrollPacket
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object KeyEventHandler {

    @SubscribeEvent
    fun handleMouseInput(event: MouseEvent) {
        if (event.dwheel == 0) return
        val player = Minecraft.getMinecraft().player
        val stack = player.heldItemMainhand
        if (!stack.isEmpty && stack.item is IItemScrollEvent && player.isSneaking) {
            val scrollDir = if (event.dwheel < 0) -1 else 1

            if (player.world.isRemote) {
                PacketHandler.sendToServer(ClientItemScrollPacket(scrollDir))
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
        val item = stack.item
        if (!stack.isEmpty && item is IItemKeyBindEvent && Minecraft.getMinecraft().inGameHasFocus) {
            if (item.getKeyBind().isPressed) {
                (stack.item as IItemKeyBindEvent).keyPress(player, stack)
                PacketHandler.sendToServer(ClientItemKeyBindPacket())
            }
        }
    }
}