package net.cydhra.technocracy.foundation.model.blocks.util

import net.minecraft.creativetab.CreativeTabs


interface IBlockMultipleCreativeTabs {
    fun isValidCreativeTab(tab: CreativeTabs): Boolean
}