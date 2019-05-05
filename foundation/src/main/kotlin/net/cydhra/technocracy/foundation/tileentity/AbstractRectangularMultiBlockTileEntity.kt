package net.cydhra.technocracy.foundation.tileentity

import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase
import net.cydhra.technocracy.foundation.tileentity.api.TCControllerTileEntity
import net.cydhra.technocracy.foundation.tileentity.api.TCMachineTileEntity
import net.minecraft.block.state.IBlockState

abstract class AbstractRectangularMultiBlockTileEntity
    : RectangularMultiblockTileEntityBase(), TCMachineTileEntity by MachineTileEntity(), TCControllerTileEntity {

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
}