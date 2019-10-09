package net.cydhra.technocracy.foundation.model.multiblock.validator

import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError
import net.minecraft.world.World

/**
 * A multiblock controller that validates a non-rectangular multiblock that consists of multiple rectangular tiles
 */
abstract class TiledMultiBlockControllerBase(world: World) : RectangularMultiblockControllerBase(world) {

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
        val maxXSize = this.maximumXSize
        val maxYSize = this.maximumYSize
        val maxZSize = this.maximumZSize
        val minXSize = this.minimumXSize
        val minYSize = this.minimumYSize
        val minZSize = this.minimumZSize
        when {
            maxXSize in 1 until deltaX -> {
                validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_large", maxXSize, "X")
                return false
            }
            maxYSize in 1 until deltaY -> {
                validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_large", maxYSize, "Y")
                return false
            }
            maxZSize in 1 until deltaZ -> {
                validatorCallback.setLastError("zerocore:api.multiblock.validation.machine_too_large", maxZSize, "Z")
                return false
            }
            deltaX < minXSize -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", minXSize, "X")
                return false
            }
            deltaY < minYSize -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", minYSize, "Y")
                return false
            }
            deltaZ < minZSize -> {
                validatorCallback.setLastError("zerocore:zerocore:api.multiblock.validation.machine_too_small", minZSize, "Z")
                return false
            }
        }

        return false
    }
}