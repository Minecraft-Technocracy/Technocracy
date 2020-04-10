package net.cydhra.technocracy.optics.client

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemStack


/**
 * Creative tab for all items of this mod
 */
val powertoolsCreativeTab = object : CreativeTabs("powertools.main") {
    init {
        this.backgroundImageName = "items.png"
    }

    override fun getTabIconItem(): ItemStack {
        return ItemStack(Items.DIAMOND_PICKAXE)
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}