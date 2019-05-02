package net.cydhra.technocracy.foundation.tileentity

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity

abstract class AbstractTileEntity : TileEntity(), TCTileEntity {

    /**
     * Cached attached block's BlockState.
     */
    protected var state: IBlockState? = null

    /**
     * Query the world for the [IBlockState] associated with this entity
     *
     * @return the block state of the associated block in world
     */
    protected fun getBlockState(): IBlockState {
        if (this.state == null) {
            this.state = this.world.getBlockState(this.getPos())
        }
        return this.state!!
    }

    /**
     * Mark the block for a block update. Does not mark the chunk dirty.
     */
    protected fun markForUpdate() {
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3)
        }
    }
}