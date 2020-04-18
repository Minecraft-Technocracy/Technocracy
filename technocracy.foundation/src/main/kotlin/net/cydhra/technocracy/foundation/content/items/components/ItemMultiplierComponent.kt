package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating the energy/fuel/input consumption multiplier of a item.
 *
 * Accessing the property [multiplier] returns the actual speedup.
 *
 * @param upgradeParameter the upgrade parameter that affects this multiplier. If set to null, this multiplier cannot
 * be modified by upgrades.
 * @param callback a callback triggered when the multiplier is changed. Can be null
 */
class ItemMultiplierComponent(
        val upgradeParameter: UpgradeParameter?,
        val callback: ((Double) -> Unit)?
) : AbstractItemComponent() {
    companion object {
        private const val NBT_KEY_ENERGY = "multiplier"
    }

    override val type: ComponentType = ComponentType.OTHER

    /**
     * Current multiplier progress per tick of the machine
     */
    var multiplier: Double = 1.0
        set(v) {
            field = v
            callback?.invoke(v)
        }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setDouble(NBT_KEY_ENERGY, this@ItemMultiplierComponent.multiplier)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.multiplier = nbt.getDouble(NBT_KEY_ENERGY)
    }
}