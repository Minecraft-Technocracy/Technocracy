package net.cydhra.technocracy.foundation.api.blocks.util

import net.minecraft.creativetab.CreativeTabs


interface IBlockMultipleCreativeTabs {
    fun isValidCreativeTab(tab: CreativeTabs): Boolean
}