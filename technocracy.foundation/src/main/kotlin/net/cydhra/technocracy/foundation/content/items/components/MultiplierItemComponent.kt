package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.model.items.capability.AbstractItemComponent
import net.cydhra.technocracy.foundation.model.upgrades.UpgradeParameter
import net.minecraft.nbt.NBTTagCompound


class MultiplierItemComponent (val upgradeParameter: UpgradeParameter?) : AbstractItemComponent() {
    companion object {
        private const val NBT_KEY_ENERGY = "multiplier"
    }

    override val type: ComponentType = ComponentType.OTHER

    /**
     * Current multiplier progress per tick of the machine
     */
    var multiplier: Double = 1.0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setDouble(NBT_KEY_ENERGY, this@MultiplierItemComponent.multiplier)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.multiplier = nbt.getDouble(NBT_KEY_ENERGY)
    }


    fun getCappedMultiplier(): Double {
        return this.multiplier.coerceAtLeast(0.1)
    }

}