package net.cydhra.technocracy.foundation.model.multiblock.api

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError
import net.minecraft.block.state.IBlockState
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
        val tempDeltaX = maxX - minX + 1
        val tempDeltaZ = maxZ - minZ + 1
        val deltaX: Int
        val deltaY = maxY - minY + 1
        val deltaZ: Int

        when {
            tempDeltaX < tileSizeZ && tempDeltaX < tileSizeX -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small",
                        tileSizeX,
                        "X")
                return false
            }
            deltaY < sizeY -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small",
                        sizeY,
                        "Y")
                return false
            }
            tempDeltaZ < tileSizeZ && tempDeltaZ < tileSizeX -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small",
                        tileSizeZ,
                        "Z")
                return false
            }
        }

        //Get actual delta values. With a tile size of 5 and 3 tiles, delta will be 13. This gets the actual value that
        //  delta would be if the tiles were next to each other without sharing blocks. E.g. 15 in this case.
        val actualDeltaX: Int? = getActualDelta(tempDeltaX, tileSizeX) ?: getActualDelta(tempDeltaX, tileSizeZ)
        val actualDeltaZ: Int? = getActualDelta(tempDeltaZ, tileSizeX) ?: getActualDelta(tempDeltaZ, tileSizeZ)

        //The next if needs the actual delta values that's why this check has to be done
        if (actualDeltaX == null || actualDeltaZ == null) {
            validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", tileSizeX, tileSizeZ, sizeY)
            return false
        }

        deltaX = actualDeltaX
        deltaZ = actualDeltaZ

        if (((deltaX % tileSizeX != 0 && deltaX % tileSizeZ != 0) ||
                        (deltaZ % tileSizeX != 0 && deltaZ % tileSizeZ != 0)) ||
                (tileSizeX != tileSizeZ &&
                        ((deltaZ % tileSizeX == 0 && deltaX % tileSizeX == 0) ||
                                (deltaZ % tileSizeZ == 0 && deltaX % tileSizeZ == 0)))) {
            validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", tileSizeX, tileSizeZ, sizeY)
            return false
        }

        //Get the tile size corresponding to each axis. Subtract one because the size is actually one block too much
        //  (e.g. if minX is 5 and sizeX is 5 then maxX should be 9 and not 10)
        var xAxisSize = (if (deltaX % tileSizeX == 0) tileSizeX else tileSizeZ) - 1
        var zAxisSize = (if (deltaZ % tileSizeX == 0) tileSizeZ else tileSizeX) - 1

        //If tileSizeX and tileSizeZ have a common multiple in deltaX or deltaZ then the tile detection has to be
        //  repeated with flipped axis, only if no tile previously was valid. This has to be done, because if that
        //  condition is true there's no way to determine the x- and zAxisSize for a delta value.
        for(i in 0..2) {
            tiles.clear()
            //maxX and Z are decremented by one because otherwise the loop would hit the edge and add another tile where
            //  where there isn't one
            for (x in minX until (maxX - 1) step xAxisSize) {
                for (z in minZ until (maxZ - 1) step zAxisSize) {
                    val pos = BlockPos(x, maxY, z)
                    //This whole code doesn't work anyway if every block is accepted as a frame
                    if (frameBlockWhitelist!!.test(this.WORLD.getBlockState(pos))) {
                        this.tiles += Pair(pos, BlockPos(x + xAxisSize, minY, z + zAxisSize))
                    }
                }
            }

            if(tiles.isEmpty()) {
                val tmp = xAxisSize
                xAxisSize = zAxisSize
                zAxisSize = tmp
            } else {
                break
            }
        }

        println("valid")
        return true
    }

    private fun getActualDelta(delta: Int, tileSize: Int): Int? {
        var tempDelta = delta
        while (!(tempDelta % tileSize == 0 && tempDelta - ((tempDelta / tileSize) - 1) == delta)) {
            tempDelta++
            if (tempDelta - ((tempDelta / tileSize) - 1) > delta)
                return null
        }
        return tempDelta
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