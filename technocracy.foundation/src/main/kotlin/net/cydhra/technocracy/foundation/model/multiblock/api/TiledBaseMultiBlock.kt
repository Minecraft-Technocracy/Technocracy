package net.cydhra.technocracy.foundation.model.multiblock.api

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

/**
 * Base class for tiled multiblocks of this mod.
 * Note: [tileSizeX] and [tileSizeZ] are not bound to an axis.
 */
abstract class TiledBaseMultiBlock(
        frameBlockWhitelist: Predicate<IBlockState>?,
        sideBlockWhitelist: Predicate<IBlockState>?,
        topBlockWhitelist: Predicate<IBlockState>?,
        bottomBlockWhitelist: Predicate<IBlockState>?,
        interiorBlockWhitelist: Predicate<IBlockState>?,
        val tileFrameBlockWhitelist: Predicate<IBlockState>?,
        val tileSideBlockWhitelist: Predicate<IBlockState>?,
        private val tileSizeX: Int,
        private val sizeY: Int,
        private val tileSizeZ: Int,
        world: World)
    : BaseMultiBlock(frameBlockWhitelist, sideBlockWhitelist, topBlockWhitelist, bottomBlockWhitelist,
        interiorBlockWhitelist, 0, 0, world) {

    //Contains min and max coordinate of each tile
    protected val tiles = mutableSetOf<Pair<BlockPos, BlockPos>>()

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (this.connectedParts.size < this.minimumNumberOfBlocksForAssembledMachine) {
            validatorCallback.lastError = ValidationError.VALIDATION_ERROR_TOO_FEW_PARTS
            return false
        }

        val maximumCoord = this.maximumCoord
        val minimumCoord = this.minimumCoord
        val minX = minimumCoord.x
        val minY = minimumCoord.y
        val minZ = minimumCoord.z
        val maxX = maximumCoord.x
        val maxY = maximumCoord.y
        val maxZ = maximumCoord.z
        val deltaX = maxX - minX + 1
        val deltaY = maxY - minY + 1
        val deltaZ = maxZ - minZ + 1
        when {
            (deltaX % tileSizeX != 0 && deltaX % tileSizeZ != 0) || (deltaZ % tileSizeX != 0 && deltaZ % tileSizeZ != 0) -> {
                validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", tileSizeX, tileSizeZ, sizeY)
                return false
            }
            //If tileSizeX and tileSizeZ are not equal then only one size belongs to one delta
            tileSizeX != tileSizeZ && ((deltaZ % tileSizeX == 0 && deltaX % tileSizeX == 0) || (deltaZ % tileSizeZ == 0 && deltaX % tileSizeZ == 0)) -> {
                validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", tileSizeX, tileSizeZ, sizeY)
                return false
            }
            deltaX < tileSizeZ -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", tileSizeX,
                        "X")
                return false
            }
            deltaY < sizeY -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", sizeY,
                        "Y")
                return false
            }
            deltaZ < tileSizeZ -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", tileSizeZ,
                        "Z")
                return false
            }
        }

        //Loop along x-axis to find first block. There should always be a block touch each side, so looping through one
        //  side is sufficient
        var pos: BlockPos? = null
        for(x in minX until maxX) {
            if(isBlockGoodForFrame(this.WORLD, x, maxY, maxZ, validatorCallback))
                pos = BlockPos(x, maxY, maxZ)
        }
        //Should be impossible
        if(pos == null)
            return false

        //Get the tile size corresponding to each axis. Subtract one because the size is actually one block too much
        //  (e.g. if minX is 5 and sizeX is 5 then maxX should be 9 and not 10)
        val xAxisSize = (if(deltaX % tileSizeX == 0) tileSizeX else tileSizeZ) - 1
        val zAxisSize = (if(deltaZ % tileSizeX == 0) tileSizeX else tileSizeZ) - 1

        //maxX and Z are decremented by one because otherwise the loop would hit the edge and add another tile where
        //  where there isn't one
        for (x in minX until (maxX - 1) step xAxisSize) {
            for (z in minZ until (maxZ - 1) step zAxisSize) {
                val state = this.WORLD.getBlockState(BlockPos(x, maxY, z))
                if(state.block == Blocks.AIR)
                    continue
            }
        }

        return true
    }

    fun isBlockGoodForTileFrame(world: World, x: Int, y: Int, z: Int): Boolean {
        return tileFrameBlockWhitelist == null || tileFrameBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    fun isBlockGoodForTileSide(world: World, x: Int, y: Int, z: Int): Boolean {
        return tileSideBlockWhitelist == null || tileSideBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun getMaximumXSize(): Int {
        return this.tileSizeX
    }

    override fun getMaximumYSize(): Int {
        return this.sizeY
    }

    override fun getMaximumZSize(): Int {
        return this.tileSizeZ
    }
}