package net.cydhra.technocracy.optics.client

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack


/**
 * Creative tab for all items of this mod
 */
val opticsCreativeTab = object : CreativeTabs("optics.main") {
    init {
        this.backgroundImageName = "items.png"
    }

    override fun getTabIconItem(): ItemStack {
        return ItemStack(ItemBlock.getItemFromBlock(Blocks.GLASS))
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}