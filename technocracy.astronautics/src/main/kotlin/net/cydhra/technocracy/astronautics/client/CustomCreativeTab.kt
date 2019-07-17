package net.cydhra.technocracy.astronautics.client

import net.cydhra.technocracy.astronautics.blocks.general.scaffoldBlock
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack


/**
 * Creative tab for all items of this mod
 */
val astronauticsCreativeTabs = object : CreativeTabs("astronautics") {
    init {
        this.backgroundImageName = "items.png"
    }

    override fun getTabIconItem(): ItemStack {
        return ItemStack(ItemBlock.getItemFromBlock(scaffoldBlock)) // TODO replace this with something more exemplary for this mod
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}