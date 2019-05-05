package net.cydhra.technocracy.foundation.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.blocks.general.BaseRotateableTileEntityBlock
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class MultiBlockBaseBlock<out T>(name: String, private val tileEntityConstructor: () -> T)
    : BaseRotateableTileEntityBlock(name, material = Material.ROCK)
        where T : TileEntity, T : IMultiblockPart {

    init {
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return tileEntityConstructor()
    }

    protected fun getMultiBlockPartTileEntity(world: World, pos: BlockPos): T {
        @Suppress("UNCHECKED_CAST")
        return world.getTileEntity(pos) as? T ?: error("there is the wrong tile entity at this location")
    }
}