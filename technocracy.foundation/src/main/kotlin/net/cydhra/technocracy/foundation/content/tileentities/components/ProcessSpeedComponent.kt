package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating the a process speed multiplier of a machine.
 */
class ProcessSpeedComponent : AbstractComponent() {
    companion object {
        private const val NBT_KEY_PROCESS_SPEED = "speed"
    }

    override val type: ComponentType = ComponentType.OTHER

    /**
     * Current multiplier progress per tick of the machine
     */
    var processMultiplier: Double = 1.0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setDouble(NBT_KEY_PROCESS_SPEED, this@ProcessSpeedComponent.processMultiplier)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.processMultiplier = nbt.getDouble(NBT_KEY_PROCESS_SPEED)
    }

}