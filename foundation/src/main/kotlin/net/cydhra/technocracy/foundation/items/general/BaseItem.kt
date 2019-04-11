package net.cydhra.technocracy.foundation.items.general

import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item

/**
 * Base class for all items added by this modification
 */
open class BaseItem(unlocalizedName: String,
                    registryName: String = unlocalizedName,
                    val oreDictName: String? = null,
                    val itemColor: IItemColor? = null) : Item() {

    open val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}