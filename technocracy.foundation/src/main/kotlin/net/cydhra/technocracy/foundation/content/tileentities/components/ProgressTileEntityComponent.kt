package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent
import net.minecraft.nbt.NBTTagCompound

class ProgressTileEntityComponent: AbstractTileEntityComponent() {

    override val type: ComponentType = ComponentType.OTHER

    /**
     * progress from 0 to 100
     */
    var progress: Int = 0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setInteger("progress", progress) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        progress = nbt.getInteger("progress")
    }

}