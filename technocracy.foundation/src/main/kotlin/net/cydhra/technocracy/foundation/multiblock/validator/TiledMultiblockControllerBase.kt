package net.cydhra.technocracy.foundation.multiblock.validator

import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.minecraft.world.World

/**
 * A multiblock controller that validates a non-rectangular multiblock that consists of multiple rectangular tiles
 */
abstract class TiledMultiBlockControllerBase(world: World) : RectangularMultiblockControllerBase(world) {

    override fun isMachineWhole(p0: IMultiblockValidator?): Boolean {
        TODO("not implemented")
    }
}