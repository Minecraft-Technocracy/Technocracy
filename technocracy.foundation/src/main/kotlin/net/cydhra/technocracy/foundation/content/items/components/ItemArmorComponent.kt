package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.util.compound
import net.cydhra.technocracy.foundation.util.get
import net.minecraft.nbt.NBTTagCompound


class ItemArmorComponent() : AbstractItemComponent() {
    override val type = ComponentType.OTHER

    var toughness = 0.0
    var armor = 0.0

    override fun serializeNBT(): NBTTagCompound {
        return compound {
            "toughness" to toughness
            "armor" to armor
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        toughness = nbt["toughness"]
        armor = nbt["armor"]
    }
}