package net.cydhra.technocracy.foundation.model.items.api

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.model.items.color.ConstantItemColor
import net.minecraft.item.ItemStack
import net.minecraft.util.text.translation.I18n

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
open class ColoredPrefixedItem(val prefix: String,
                               val ingotMaterialName: String,
                               color: ConstantItemColor,
                               val isFromOreSystem: Boolean = false)
    : BaseItem("$prefix.$ingotMaterialName", oreDictName = "$prefix${ingotMaterialName.capitalize()}", itemColor = color) {

    override val modelLocation: String = "${TCFoundation.MODID}:$prefix"

    override fun getItemStackDisplayName(stack: ItemStack): String {
        if (!isFromOreSystem)
            return super.getItemStackDisplayName(stack)

        return String.format(I18n.translateToLocal("system.$prefix.name"), I18n.translateToLocal("system.$ingotMaterialName"))
    }
}