package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
class AlloyItem(ingotMaterialName: String, color: ConstantItemColor)
    : ColoredItem("ingot", ingotMaterialName, color) {

    override val modelLocation: String = "${TCFoundation.MODID}:alloy"
}