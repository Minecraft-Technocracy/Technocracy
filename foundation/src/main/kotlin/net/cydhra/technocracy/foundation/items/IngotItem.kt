package net.cydhra.technocracy.foundation.items

import net.minecraft.item.Item

/**
 * Any additional ingots created by this mod are ultimatly an instance of this class
 */
open class IngotItem(ingotMaterialName: String) : BaseItem("ingot.$ingotMaterialName") {

}