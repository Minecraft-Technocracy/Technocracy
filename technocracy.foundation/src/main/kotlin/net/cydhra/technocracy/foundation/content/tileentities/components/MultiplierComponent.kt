package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter
import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating the energy/fuel/input consumption multiplier of a machine. The
 * component's multiplier cannot be lowered below 0.1, because speed up of different machine multipliers shall not
 * reduce something to or below zero. This is done to ensure energy is not accidentally created and all components of
 * the machine function as designed.
 *
 * Accessing the property [multiplier] returns the actual speedup, using [getCappedMultiplier] will yield no less than 0.1.
 *
 * @param upgradeParameter the upgrade parameter that affects this multiplier. If set to null, this multiplier cannot
 * be modified by upgrades.
 */
class MultiplierComponent(val upgradeParameter: MachineUpgradeParameter?) : AbstractComponent() {
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
            setDouble(NBT_KEY_ENERGY, this@MultiplierComponent.multiplier)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.multiplier = nbt.getDouble(NBT_KEY_ENERGY)
    }

    fun getCappedMultiplier(): Double {
        return this.multiplier.coerceAtLeast(0.1)
    }

}