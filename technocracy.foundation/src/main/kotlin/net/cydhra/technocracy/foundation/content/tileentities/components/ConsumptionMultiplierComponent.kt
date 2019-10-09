package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating the energy/fuel/input consumption multiplier of a machine.
 */
class ConsumptionMultiplierComponent : AbstractComponent() {
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
            setDouble(NBT_KEY_ENERGY, this@ConsumptionMultiplierComponent.energyMultiplier)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.energyMultiplier = nbt.getDouble(NBT_KEY_ENERGY)
    }

}