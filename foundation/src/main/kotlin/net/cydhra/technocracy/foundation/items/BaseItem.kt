package net.cydhra.technocracy.foundation.items

import net.minecraft.item.Item

/**
 * Base class for all items added by this modification
 */
abstract class BaseItem(unlocalizedName: String, registryName: String = unlocalizedName) : Item() {

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}