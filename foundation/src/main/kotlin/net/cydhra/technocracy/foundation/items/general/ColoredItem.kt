package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.minecraft.creativetab.CreativeTabs

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
open class ColoredItem(prefix: String, ingotMaterialName: String, color: ConstantItemColor)
    : BaseItem("$prefix.$ingotMaterialName", oreDictName = "$prefix${ingotMaterialName.capitalize()}", itemColor = color) {

    override val modelLocation: String = "${TCFoundation.MODID}:$prefix"

    init {
        this.creativeTab = CreativeTabs.MATERIALS
    }
}