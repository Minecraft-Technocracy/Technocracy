package net.cydhra.technocracy.foundation.content.items.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack


interface IItemScrollEvent {
    fun mouseScroll(player: EntityPlayer, itemStack: ItemStack, dir: Int)
}