package net.cydhra.technocracy.foundation.client

import net.cydhra.technocracy.foundation.materialsystems.osmiumSystem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack

/**
 * Creative tab for all items of this mod. Since this are potentially many, a search bar is displayed
 */
val technocracyCreativeTabs = object : CreativeTabs("technocracy") {
    init {
        this.backgroundImageName = "item_search.png"
    }

    override fun getTabIconItem(): ItemStack {
        return ItemStack(osmiumSystem.gear) // TODO replace this with something more exemplary for this mod
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}