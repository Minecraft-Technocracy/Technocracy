package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import kotlin.math.roundToInt

/**
 * A logic that consumes an additive to a machine and disables the machine progress if no additive is remaining
 */
class AdditiveConsumptionLogic(private val additiveComponent: FluidTileEntityComponent,
                               private val baseConsumption: Int,
                               private val multiplier: MultiplierTileEntityComponent) : ILogic {
    override fun preProcessing(): Boolean {
        return additiveComponent.fluid.currentFluid?.amount ?: 0 >= baseConsumption
    }

    override fun processing() {
        additiveComponent.fluid.drain((baseConsumption * this.multiplier.multiplier).roundToInt(), true)
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }
}