package net.cydhra.technocracy.foundation.client

import net.cydhra.technocracy.foundation.items.general.facadeItem
import net.cydhra.technocracy.foundation.materialsystems.niobiumSystem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
    override fun getTabIconItem(): ItemStack {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var stack: ItemStack
    var last: Long = -1

    init {
        this.backgroundImageName = "items.png"

    }

    @SideOnly(Side.CLIENT)
    override fun getIconItemStack(): ItemStack {
        if(last < System.currentTimeMillis()) {
            last = System.currentTimeMillis() + 1000 * 5
            stack = facadeItem.getRandomFacade()
        }
        return stack
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}