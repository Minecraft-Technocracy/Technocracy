package net.cydhra.technocracy.foundation.model.multiblock.api

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

/**
 * Base class for tiled multiblocks of this mod. Extends
 */
abstract class TiledBaseMultiBlock(
        frameBlockWhitelist: Predicate<IBlockState>?,
        sideBlockWhitelist: Predicate<IBlockState>?,
        topBlockWhitelist: Predicate<IBlockState>?,
        bottomBlockWhitelist: Predicate<IBlockState>?,
        interiorBlockWhitelist: Predicate<IBlockState>?,
        private val sizeX: Int,
        private val sizeY: Int,
        private val sizeZ: Int,
        world: World)
    : BaseMultiBlock(frameBlockWhitelist, sideBlockWhitelist, topBlockWhitelist, bottomBlockWhitelist, interiorBlockWhitelist, 0, 0, world) {

    //Contains min and max coordinate of each tile
    protected val tiles = mutableListOf<Pair<BlockPos, BlockPos>>()

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
            deltaX % sizeX != 0 -> {
                validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", sizeX, sizeZ, sizeY)
                return false
            }
            deltaY % sizeY != 0 -> {
                validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", sizeX, sizeZ, sizeY)
                return false
            }
            deltaZ % sizeZ != 0 -> {
                validatorCallback.setLastError("multiblock.error.inconsistent_tile_size", sizeX, sizeZ, sizeY)
                return false
            }
            deltaX < sizeZ -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", sizeX, "X")
                return false
            }
            deltaY < sizeY -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", sizeY, "Y")
                return false
            }
            deltaZ < sizeZ -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", sizeZ, "Z")
                return false
            }
        }

        return true
    }

    override fun getMaximumXSize(): Int {
        return this.sizeX
    }

    override fun getMaximumYSize(): Int {
        return this.sizeY
    }

    override fun getMaximumZSize(): Int {
        return this.sizeZ
    }
}