package net.cydhra.technocracy.foundation.content.items.util

import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


interface IItemKeyBindEvent {

    @SideOnly(Side.CLIENT)
    fun getKeyBind() : KeyBinding

    fun keyPress(player: EntityPlayer, itemStack: ItemStack)
}