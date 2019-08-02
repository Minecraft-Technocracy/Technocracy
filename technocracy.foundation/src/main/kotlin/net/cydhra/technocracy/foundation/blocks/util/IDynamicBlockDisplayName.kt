package net.cydhra.technocracy.foundation.blocks.util

import net.minecraft.item.ItemStack


interface IDynamicBlockDisplayName {
    fun getUnlocalizedName(stack: ItemStack): String
}