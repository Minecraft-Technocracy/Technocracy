package net.cydhra.technocracy.foundation.model.tileentities.api.components

import net.cydhra.technocracy.foundation.model.components.IComponent
import net.minecraft.tileentity.TileEntity

/**
 * Class defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
abstract class AbstractComponent : IComponent {

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

    override fun onRegister() {
        // do nothing by default
    }
}

