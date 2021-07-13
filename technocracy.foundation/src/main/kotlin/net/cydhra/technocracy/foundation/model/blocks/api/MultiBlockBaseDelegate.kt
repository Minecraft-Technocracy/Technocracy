package net.cydhra.technocracy.foundation.model.blocks.api

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class MultiBlockBaseDelegate<out T>(private val tileEntityConstructor: () -> T)
    : TCMultiBlock<T> where T : TileEntity, T : IMultiblockPart {

    override fun createNewTileEntity(worldIn: World, meta: Int): T {
        return tileEntityConstructor()
    }

    override fun getMultiBlockPartTileEntity(world: World, pos: BlockPos): T {
        @Suppress("UNCHECKED_CAST")
        return world.getTileEntity(pos) as? T ?: error("there is the wrong tile entity at this location")
    }
}