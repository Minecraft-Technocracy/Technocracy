package net.cydhra.technocracy.foundation.blocks.api

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface TCMultiBlock<out T> where T : TileEntity, T : IMultiblockPart {
    fun createNewTileEntity(worldIn: World, meta: Int): T

    fun getMultiBlockPartTileEntity(world: World, pos: BlockPos): T
}