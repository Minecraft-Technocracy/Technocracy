package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.content.tileentities.components.RedstoneModeTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A logic that consumes an additive to a machine and disables the machine progress if no additive is remaining
 */
class RedstoneLogic(private val world: World,
                    private val pos: BlockPos,
                    private val redstoneModeComponent: RedstoneModeTileEntityComponent) : ILogic {
    override fun preProcessing(): Boolean {
        return when (redstoneModeComponent.redstoneMode) {
            RedstoneModeTileEntityComponent.RedstoneMode.HIGH -> world.getStrongPower(pos) > 0
            RedstoneModeTileEntityComponent.RedstoneMode.LOW -> world.getStrongPower(pos) == 0
            RedstoneModeTileEntityComponent.RedstoneMode.IGNORE -> true
        }
    }

    override fun processing() {

    }

    override fun postProcessing(wasProcessing: Boolean) {

    }
}