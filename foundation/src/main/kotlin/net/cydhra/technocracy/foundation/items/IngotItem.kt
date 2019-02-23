package net.cydhra.technocracy.foundation.items

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
open class IngotItem(ingotMaterialName: String, color: ConstantItemColor)
    : BaseItem("ingot.$ingotMaterialName", itemColor = color)