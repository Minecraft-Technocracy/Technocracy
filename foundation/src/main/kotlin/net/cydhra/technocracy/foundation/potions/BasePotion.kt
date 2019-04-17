package net.cydhra.technocracy.foundation.potions

import net.minecraft.potion.Potion
import net.minecraftforge.common.MinecraftForge


open class BasePotion(potionName: String, isBadPotion: Boolean, color: Int) : Potion(isBadPotion, color) {
    init {
        this.setPotionName(potionName)
        setRegistryName("effect.$potionName")
        MinecraftForge.EVENT_BUS.register(this)
    }
}