package net.cydhra.technocracy.foundation.tileentity

import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

abstract class AbstractRectangularMultiBlockControllerTileEntity(
        private val frameBlockWhitelist: Predicate<IBlockState>?,
        private val sideBlockWhitelist: Predicate<IBlockState>?,
        private val topBlockWhitelist: Predicate<IBlockState>?,
        private val bottomBlockWhitelist: Predicate<IBlockState>?,
        private val interiorBlockWhitelist: Predicate<IBlockState>?,
        private val maximumSizeXZ: Int,
        private val maximumSizeY: Int,
        world: World)
    : RectangularMultiblockControllerBase(world), TCMachineTileEntity by MachineTileEntity() {

    override fun isBlockGoodForSides(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return sideBlockWhitelist == null || sideBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForFrame(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return frameBlockWhitelist == null || frameBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForTop(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return topBlockWhitelist == null || topBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForInterior(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return interiorBlockWhitelist == null || interiorBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForBottom(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return bottomBlockWhitelist == null || bottomBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun getMaximumXSize(): Int {
        return this.maximumSizeXZ
    }

    override fun getMaximumYSize(): Int {
        return this.maximumSizeY
    }

    override fun getMaximumZSize(): Int {
        return this.maximumSizeXZ
    }

}