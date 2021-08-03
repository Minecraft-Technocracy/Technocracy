package net.cydhra.technocracy.foundation.content.blocks.wrapper

import net.cydhra.technocracy.foundation.content.tileentities.TileTileWrapper
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


class TileWrapperBlock : BlockWrapperBlock("tilewrapper") {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileTileWrapper()
    }
}