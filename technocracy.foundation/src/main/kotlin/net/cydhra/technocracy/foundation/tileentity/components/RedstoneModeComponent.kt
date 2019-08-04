package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent.RedstoneMode
import net.minecraft.nbt.NBTTagCompound

/**
 * A component for machines to accept redstone signals as a on/off switch.
 *
 * @see RedstoneMode
 */
class RedstoneModeComponent : AbstractComponent() {

    override val type: ComponentType = ComponentType.REDSTONE_MODE

    /**
     * The current set mode on the machine.
     */
    var redstoneMode = RedstoneMode.IGNORE

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setInteger("type", redstoneMode.ordinal) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        redstoneMode = RedstoneMode.values()[nbt.getInteger("type")]
    }

    /**
     * All redstone modes a machine can support.
     */
    enum class RedstoneMode {
        /**
         * High redstone mode requires full redstone signal strength for the machine to work
         */
        HIGH,

        /**
         * Low redstone mode requires any redstone signal strength other than zero for the machine to work
         */
        LOW,

        /**
         * The redstone input to the machine is ignored - the machine is always active
         */
        IGNORE
    }
}