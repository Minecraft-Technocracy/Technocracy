package net.cydhra.technocracy.foundation.items.general

import net.minecraft.item.ItemStack


interface IDynamicStackDisplayName {
    fun getUnlocalizedName(stack: ItemStack): String
}