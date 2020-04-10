package net.cydhra.technocracy.foundation.model.tileentities.api.components

import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.minecraft.tileentity.TileEntity

/**
 * Class defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
abstract class AbstractTileEntityComponent : IComponent {

    lateinit var tile: TileEntity

    var syncToClient = false
    var allowAutoSave = true

    open fun markDirty(needsClientRerender: Boolean = false) {
        if (allowAutoSave) {
            if (syncToClient && needsClientRerender) {
                notifyBlockUpdate()
            }
            tile.markDirty()
        }
    }

    fun notifyBlockUpdate() {
        val state = tile.world.getBlockState(tile.pos)
        tile.world.markAndNotifyBlock(tile.pos, tile.world.getChunkFromBlockCoords(tile.pos), state, state, 3)
    }

    override fun onRegister() {
        // do nothing by default
    }

    override fun onLoadAggregate() {
        // do nothing by default
    }
}

