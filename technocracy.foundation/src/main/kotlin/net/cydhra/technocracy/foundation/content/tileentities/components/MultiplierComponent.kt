package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating the energy/fuel/input consumption multiplier of a machine.
 */
class MultiplierComponent : AbstractComponent() {
    companion object {
        private const val NBT_KEY_ENERGY = "energy"
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

}