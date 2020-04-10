package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.content.tileentities.components.RedstoneModeTileEntityComponent.RedstoneMode
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraft.nbt.NBTTagCompound

/**
 * A component for machines to accept redstone signals as a on/off switch.
 *
 * @see RedstoneMode
 */
class RedstoneModeTileEntityComponent : AbstractTileEntityComponent() {

    override val type: ComponentType = ComponentType.OTHER

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