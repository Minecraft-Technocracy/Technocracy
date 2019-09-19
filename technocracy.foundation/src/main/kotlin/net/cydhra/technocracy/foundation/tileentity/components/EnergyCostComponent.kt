package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating the energy consumption multiplier of a machine.
 */
class EnergyCostComponent : AbstractComponent() {
    companion object {
        private const val NBT_KEY_ENERGY = "energy"
    }

    override val type: ComponentType = ComponentType.OTHER

    /**
     * Current multiplier progress per tick of the machine
     */
    var energyMultiplier: Double = 1.0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setDouble(NBT_KEY_ENERGY, this@EnergyCostComponent.energyMultiplier)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.energyMultiplier = nbt.getDouble(NBT_KEY_ENERGY)
    }

}