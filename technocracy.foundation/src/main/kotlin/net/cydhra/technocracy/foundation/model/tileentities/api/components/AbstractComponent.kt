package net.cydhra.technocracy.foundation.model.tileentities.api.components

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 * Class defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
abstract class AbstractComponent {

    lateinit var tile: TileEntity

    var syncToClient = false
    var allowAutoSave = true

    open fun markDirty(needsClientRerender: Boolean = false) {
        if (allowAutoSave) {
            if (syncToClient && needsClientRerender) {
                val state = tile.world.getBlockState(tile.pos)
                tile.world.notifyBlockUpdate(tile.pos, state, state, 0)
            }
            tile.markDirty()
        }
    }

    abstract val type: ComponentType

    /**
     * Write the component's state to an NBT value
     */
    abstract fun serializeNBT(): NBTTagCompound

    /**
     * deserialize and apply the component's state from the given tag
     */
    abstract fun deserializeNBT(nbt: NBTTagCompound)

    /**
     * called while the component is getting registered
     */
    open fun onRegister() {
    }

}

