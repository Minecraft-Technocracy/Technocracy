package net.cydhra.technocracy.foundation.model.multiblock.api

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

/**
 * Base class for tiled multiblocks of this mod.
 *
 * @param tileFrameBlockWhitelist A whitelist for the frame of each tile. The outer most frame around the whole
 * structure is covered by [frameBlockWhitelist]
 * @param tileSideBlockWhitelist A whitelist for the frame of each tile. The outer most frame around the whole
 * structure is covered by [sideBlockWhitelist]
 * @param tileSizeX The width or length of each tile. Not bound to an axis
 * @param tileSizeZ The width or length of each tile. Not bound to an axis
 * @param sizeY The height of each tile
 */
abstract class TiledBaseMultiBlock(
        frameBlockWhitelist: Predicate<IBlockState>,
        sideBlockWhitelist: Predicate<IBlockState>?,
        topBlockWhitelist: Predicate<IBlockState>?,
        bottomBlockWhitelist: Predicate<IBlockState>,
        interiorBlockWhitelist: Predicate<IBlockState>?,
        val tileFrameBlockWhitelist: Predicate<IBlockState>?,
        val tileSideBlockWhitelist: Predicate<IBlockState>?,
        private val tileSizeX: Int,
        private val sizeY: Int,
        private val tileSizeZ: Int,
        world: World)
    : BaseMultiBlock(frameBlockWhitelist, sideBlockWhitelist, topBlockWhitelist, bottomBlockWhitelist,
        interiorBlockWhitelist, 0, 0, world) {

    /**
     * Contains all tiles belonging to this multi block in row-major order
     */
    protected val tiles = mutableSetOf<Tile>()

    init {
        if (tileSizeX < 3 || tileSizeZ < 3) {
            throw IllegalStateException("Tiles need at least a size of 3x3!")
        }
    }

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
        var zAxisSize = (if (deltaZ % tileSizeZ == 0) tileSizeZ else tileSizeX) - 1

        //Error in first iteration
        var error1 = false
        //Error in second iteration
        var error2 = false

        //Used to make sure the checked blocks belong to this multi block
        val connectedPositions = this.connectedParts.map { it.worldPosition }

        //If tileSizeX and tileSizeZ have a common multiple in deltaX or deltaZ then the tile detection has to be
        //  repeated with flipped axis, only if no tile previously was valid. This has to be done, because if that
        //  condition is true there's no way to determine the AxisSize for a delta value.
        for (i in 0 until 2) {
            tiles.clear()
            //maxX and Z are decremented by one because otherwise the loop would hit the edge and add another tile where
            //  where there isn't one
            for (x in minX until (maxX - 1) step xAxisSize) {
                for (z in minZ until (maxZ - 1) step zAxisSize) {
                    //First corner
                    val minPos = BlockPos(x, minY, z)
                    //One block further east or south
                    val minPosClose: BlockPos = minPos.east(1)
                    //East or south corner
                    val minPosFar: BlockPos =  minPos.east(xAxisSize)
                    //Center block
                    val centerPos = minPos.south(zAxisSize / 2).east(xAxisSize / 2)

                    //Checks the east of the corner block, the east corner block, and the center block to
                    //  hopefully predict a tile being there. Also makes sure the blocks are not from another multi block.
                    //
                    //  Could be made more efficient by checking all blocks in the area of the tile, but the performance
                    //  decrease isn't worth it
                    if ((frameBlockWhitelist!!.test(this.WORLD.getBlockState(minPosClose)) &&
                                    connectedPositions.contains(minPosClose)) &&
                            (frameBlockWhitelist.test(this.WORLD.getBlockState(minPosFar)) &&
                                    connectedPositions.contains(minPosFar)) &&
                            (bottomBlockWhitelist!!.test(this.WORLD.getBlockState(centerPos)) &&
                                    connectedPositions.contains(centerPos))) {
                        this.tiles += Tile(minPos, BlockPos(x + xAxisSize, maxY, z + zAxisSize))
                    }
                }
            }

            //Calculate adjacent tiles for each tile. Required to know if a frame block should be checked using
            //  frameBlockWhitelist or tileFrameBlockWhitelist
            tiles.forEach {
                tiles.forEach { other ->
                    if (it != other) {
                        if (it.maxPos.x == other.maxPos.x) {
                            if (it.maxPos.z - other.maxPos.z == zAxisSize)
                                it.adjacentTileSides += EnumFacing.NORTH
                            else if (other.maxPos.z - it.maxPos.z == zAxisSize)
                                it.adjacentTileSides += EnumFacing.SOUTH
                        } else if (it.maxPos.z == other.maxPos.z) {
                            if (it.maxPos.x - other.maxPos.x == xAxisSize)
                                it.adjacentTileSides += EnumFacing.WEST
                            else if (other.maxPos.x - it.maxPos.x == xAxisSize)
                                it.adjacentTileSides += EnumFacing.EAST
                        }
                    }
                }
            }

            //Validate each block of each tile
            tiles.forEach outer@{
                for (x in it.minPos.x..it.maxPos.x) {
                    for (y in it.minPos.y..it.maxPos.y) {
                        for (z in it.minPos.z..it.maxPos.z) {
                            val pos = BlockPos(x, y, z)
                            var whitelist: Predicate<IBlockState>?
                            //Get correct tester
                            whitelist = when {
                                x == it.maxPos.x -> getWhitelistForSideBlock(it, pos,
                                        it.adjacentTileSides.firstOrNull { s -> s == EnumFacing.EAST })
                                x == it.minPos.x -> getWhitelistForSideBlock(it, pos,
                                        it.adjacentTileSides.firstOrNull { s -> s == EnumFacing.WEST })
                                z == it.minPos.z -> getWhitelistForSideBlock(it, pos,
                                        it.adjacentTileSides.firstOrNull { s -> s == EnumFacing.NORTH })
                                z == it.maxPos.z -> getWhitelistForSideBlock(it, pos,
                                        it.adjacentTileSides.firstOrNull { s -> s == EnumFacing.SOUTH })
                                y == it.maxPos.y -> topBlockWhitelist
                                y == it.minPos.y -> bottomBlockWhitelist
                                else -> interiorBlockWhitelist
                            }

                            //Use tester to test the block
                            if (whitelist != null && !whitelist.test(WORLD.getBlockState(pos))) {
                                //TODO: Error message can be unprecise in some cases. Only thing to add could be to save
                                //  the amount of valid blocks and tiles for both iterations and then check which one had
                                //  more valid stuff. The error message of that iteration could then be expected to be
                                //  more accurate.
                                validatorCallback.setLastError("multiblock.error.invalid_block", pos.x, pos.y, pos.z)
                                if (i == 0)
                                    error1 = true
                                else
                                    error2 = true
                                return@outer
                            }
                        }
                    }
                }
            }

            //If the axis sizes aren't the same, the other option (axis flipped) has to be evaluated
            if (i == 0 && xAxisSize != zAxisSize && (error1 || tiles.isEmpty())) {
                //Switch axis and try again
                val tmp = xAxisSize
                xAxisSize = zAxisSize
                zAxisSize = tmp
            } else {
                //The tiles list is not allowed to be empty at this point, so just force an error
                if (tiles.isEmpty()) {
                    error2 = true
                    validatorCallback.setLastError("multiblock.error.no_tile_found")
                }
                break
            }
        }

        //Make sure one is valid. First part is for multi blocks with the same allowed x and z length
        return (xAxisSize == zAxisSize && !error1 && tiles.isNotEmpty()) || ((xAxisSize != zAxisSize) && !(error1 && error2))
    }

    /**
     * Finds the correct whitelist that should be used for a block of a tile, that is located on the NORTH, EAST, SOUTH
     * or WEST side of the tile (Either frame or actual side)
     *
     * @param tile The tile that the block is in
     * @param pos The [BlockPos] that should be validated
     * @param adjacentSide The facing of the side that the block is in; or *null* if the side of this block does not
     * have another side adjacent to it. Only allowed values are NORTH, EAST, SOUTH and WEST
     * @return The proper whitelist that should be used for the given block
     */
    private fun getWhitelistForSideBlock(tile: Tile, pos: BlockPos,
                                         adjacentSide: EnumFacing?): Predicate<IBlockState>? {
        val hasAdjacent = adjacentSide != null
        val isFrame =
                ((pos.x == tile.maxPos.x || pos.x == tile.minPos.x) && //East and west
                        (pos.y == tile.maxPos.y || pos.y == tile.minPos.y) || //Top bottom
                        (pos.z == tile.maxPos.z || pos.z == tile.minPos.z)) || //North and south frame
                        ((pos.z == tile.maxPos.z || pos.z == tile.minPos.z) && //North and south
                                (pos.y == tile.maxPos.y || pos.y == tile.minPos.y) || //Top bottom
                                (pos.x == tile.maxPos.x || pos.x == tile.minPos.x)) //East and west frame

        //Frame
        if (isFrame) {
            //The first corner when going counter-clockwise the side
            val firstCorner = when (adjacentSide) {
                EnumFacing.NORTH -> pos.x == tile.minPos.x
                EnumFacing.EAST -> pos.z == tile.minPos.z
                EnumFacing.SOUTH -> pos.x == tile.maxPos.x
                EnumFacing.WEST -> pos.z == tile.maxPos.z
                null -> false
                else -> throw IllegalArgumentException("adjacentSide has to be NORTH, EAST, SOUTH or WEST!")
            }
            //The first corner when going clockwise from the side
            val secondCorner = when (adjacentSide) {
                EnumFacing.NORTH -> pos.x == tile.maxPos.x
                EnumFacing.EAST -> pos.z == tile.maxPos.z
                EnumFacing.SOUTH -> pos.x == tile.minPos.x
                EnumFacing.WEST -> pos.z == tile.minPos.z
                null -> false
                else -> throw IllegalArgumentException("adjacentSide has to be NORTH, EAST, SOUTH or WEST!")
            }
            val isCorner = firstCorner || secondCorner

            //Use tile frame if a corner is surrounded on both sides or on that side another tile is adjacent and the
            // block is not a corner
            if (hasAdjacent &&
                    ((firstCorner && tile.adjacentTileSides.contains(adjacentSide!!.rotateYCCW())) ||
                            (secondCorner && tile.adjacentTileSides.contains(adjacentSide!!.rotateY())) ||
                            (!isCorner && tileFrameBlockWhitelist != null)) //Any other frame part
            ) {
                return tileFrameBlockWhitelist
            } else if (((!hasAdjacent) || (hasAdjacent && isCorner)) &&
                    frameBlockWhitelist != null) {
                //Use normal frame. Happens for corners that are not surrounded on both sides
                // or when there's no adjacent tile on this side
                return frameBlockWhitelist
            }
        } else { //Side
            return if (hasAdjacent) tileSideBlockWhitelist else sideBlockWhitelist
        }

        throw IllegalArgumentException("$pos is not in frame or side")
    }

    /**
     * This method tries to determine the actual delta for tileSize, as if no tiles would share blocks.
     *
     * E.g. delta: 22, tileSize: 8 -> actualDelta: 24; this is because with a delta of 24 and a tileSize of 8, there are
     * three tiles. Tile 1 and 2 and tile 2 and 3 have common blocks, that's why the original delta is two less than 24.
     *
     * @param delta The original delta value
     * @param tileSize the known tileSize
     * @return the actual delta corresponding to the tile size or *null* if [delta] and [tileSize] do not correspond to
     * each other
     */
    private fun getActualDelta(delta: Int, tileSize: Int): Int? {
        var tempDelta = delta
        while (!(tempDelta % tileSize == 0 && tempDelta - ((tempDelta / tileSize) - 1) == delta)) {
            tempDelta++
            if (tempDelta - ((tempDelta / tileSize) - 1) > delta)
                return null
        }
        return tempDelta
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

class Tile(val minPos: BlockPos, val maxPos: BlockPos) {

    /**
     * A list of directions in which a tile is adjacent.
     * Can only contain NORTH, EAST, SOUTH or WEST.
     */
    val adjacentTileSides = mutableSetOf<EnumFacing>()

    override fun hashCode(): Int {
        return minPos.hashCode() * 31 + maxPos.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tile

        return minPos.x == other.minPos.x && minPos.y == other.minPos.y && minPos.z == other.minPos.z &&
                maxPos.x == other.maxPos.x && maxPos.y == other.maxPos.y && maxPos.z == other.maxPos.z
    }

    override fun toString(): String {
        return "Tile(minPos=$minPos, maxPos=$maxPos, adjacentTileSides=$adjacentTileSides)"
    }
}