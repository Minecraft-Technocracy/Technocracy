package net.cydhra.technocracy.foundation.items.general

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack


interface IItemKeyBindEvent {
    fun keyPress(player: EntityPlayer, itemStack: ItemStack)
}