package net.cydhra.technocracy.foundation.items

import net.minecraft.creativetab.CreativeTabs

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
open class IngotItem(ingotMaterialName: String, color: ConstantItemColor)
    : BaseItem("ingot.$ingotMaterialName", itemColor = color) {

    init {
        this.creativeTab = CreativeTabs.MATERIALS
    }
}