package net.cydhra.technocracy.foundation.items

import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item

/**
 * Base class for all items added by this modification
 */
abstract class BaseItem(unlocalizedName: String,
                        registryName: String = unlocalizedName,
                        val itemColor: IItemColor? = null) : Item() {

    open val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}