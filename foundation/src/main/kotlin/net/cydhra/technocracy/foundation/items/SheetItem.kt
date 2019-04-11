package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.minecraft.creativetab.CreativeTabs

class SheetItem(ingotMaterialName: String, color: ConstantItemColor)
    : BaseItem("sheet.$ingotMaterialName", itemColor = color) {

    override val modelLocation: String = "${TCFoundation.MODID}:sheet"

    init {
        this.creativeTab = CreativeTabs.MATERIALS
    }
}