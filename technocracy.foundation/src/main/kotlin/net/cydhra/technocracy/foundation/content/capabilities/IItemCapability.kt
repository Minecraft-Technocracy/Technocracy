package net.cydhra.technocracy.foundation.content.capabilities

import net.minecraft.item.ItemStack


interface IItemCapability {
    fun getContainer(): ItemStack
}