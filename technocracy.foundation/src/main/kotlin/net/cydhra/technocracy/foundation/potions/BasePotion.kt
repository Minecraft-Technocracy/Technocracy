package net.cydhra.technocracy.foundation.potions

import net.minecraft.potion.Potion

/**
 * Base class to potion effects added by Technocracy.
 *
 * @param potionName unlocalized name for the potion
 * @param isBadPotion whether the potion effect is considered harmful or useful
 * @param color potion color multiplier
 */
open class BasePotion(potionName: String, isBadPotion: Boolean, color: Int) : Potion(isBadPotion, color) {
    init {
        this.setPotionName(potionName)
        setRegistryName("effect.$potionName")
    }
}