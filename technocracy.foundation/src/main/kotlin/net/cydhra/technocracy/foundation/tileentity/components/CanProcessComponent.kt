package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores a value indicating whether a machine is currently able to process. This ability can be
 * modified by different logic implementations, like redstone logic, heat logic and so on.
 */
class CanProcessComponent : AbstractComponent() {
    companion object {
        private const val NBT_KEY_CAN_PROCESS = "can_process"
    }

    override val type: ComponentType = ComponentType.OTHER

    /**
     * Whether the aggregatable is able to do processing at the moment
     */
    var canProcess: Boolean = true

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setBoolean(NBT_KEY_CAN_PROCESS, this@CanProcessComponent.canProcess)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.canProcess = nbt.getBoolean(NBT_KEY_CAN_PROCESS)
    }

}