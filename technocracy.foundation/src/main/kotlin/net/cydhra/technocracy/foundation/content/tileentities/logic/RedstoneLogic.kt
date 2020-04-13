package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityRedstoneModeComponent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A logic that consumes an additive to a machine and disables the machine progress if no additive is remaining
 */
class RedstoneLogic(private val world: World,
                    private val pos: BlockPos,
                    private val redstoneModeComponent: TileEntityRedstoneModeComponent) : ILogic {
    override fun preProcessing(): Boolean {
        return when (redstoneModeComponent.redstoneMode) {
            TileEntityRedstoneModeComponent.RedstoneMode.HIGH -> world.getStrongPower(pos) > 0
            TileEntityRedstoneModeComponent.RedstoneMode.LOW -> world.getStrongPower(pos) == 0
            TileEntityRedstoneModeComponent.RedstoneMode.IGNORE -> true
        }
    }

    override fun processing() {

    }

    override fun postProcessing(wasProcessing: Boolean) {

    }
}