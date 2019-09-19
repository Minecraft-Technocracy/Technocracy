package net.cydhra.technocracy.foundation.tileentity.logic

import net.cydhra.technocracy.foundation.tileentity.components.CanProcessComponent
import net.cydhra.technocracy.foundation.tileentity.components.ConsumptionMultiplierComponent
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import kotlin.math.roundToInt

/**
 * A logic that consumes an additive to a machine and disables the machine progress if no additive is remaining
 */
class AdditiveConsumptionLogic(private val additiveComponent: FluidComponent,
                               private val additiveProcessingLock: CanProcessComponent,
                               private val baseConsumption: Int,
                               private val consumptionMultiplier: ConsumptionMultiplierComponent) : ILogic {

    override fun update() {
        if (this.additiveProcessingLock.isProcessing) {
            additiveComponent.fluid.drain((baseConsumption * this.consumptionMultiplier.energyMultiplier).roundToInt(), true)
        }

        additiveProcessingLock.canProcess = additiveComponent.fluid.currentFluid?.amount ?: 0 >= baseConsumption
    }

}