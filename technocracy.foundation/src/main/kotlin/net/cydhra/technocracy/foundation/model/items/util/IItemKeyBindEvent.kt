package net.cydhra.technocracy.foundation.model.items.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack


interface IItemKeyBindEvent {
    fun keyPress(player: EntityPlayer, itemStack: ItemStack)
}