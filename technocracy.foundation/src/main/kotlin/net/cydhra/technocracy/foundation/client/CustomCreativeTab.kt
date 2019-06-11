package net.cydhra.technocracy.foundation.client

import net.cydhra.technocracy.foundation.items.general.FacadeItem
import net.cydhra.technocracy.foundation.items.general.facadeItem
import net.cydhra.technocracy.foundation.materialsystems.niobiumSystem
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
        return ItemStack(niobiumSystem.gear) // TODO replace this with something more exemplary for this mod
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}

val technocracyFacadeCreativeTab = object : CreativeTabs("facades") {
    init {
        this.backgroundImageName = "items.png"
    }

    override fun getTabIconItem(): ItemStack {
        return facadeItem.getRandomFacade()
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}