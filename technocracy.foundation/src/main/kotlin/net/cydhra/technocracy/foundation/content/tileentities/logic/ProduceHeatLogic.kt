package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityMultiplierComponent

/**
 * A logic that generates heat using energy. How much heat is generated depends on the speed of the machine. This
 * should not be used for generating heat as a byproduct, because it will run as long as energy is available.
 *
 * @param baseEnergyConsumption how much rf per tick is consumed (and thus converted into heat)
 * @param speedMultiplierComponent the speed multiplier of the machine
 * @param heatBuffer the heat buffer of the machine.
 */
class ProduceHeatLogic(
    private val baseEnergyConsumption: Int,
    private val speedMultiplierComponent: TileEntityMultiplierComponent,
    private val heatBuffer: TileEntityHeatStorageComponent,
    private val energyComponent: TileEntityEnergyStorageComponent,
) : ILogic<ILogicParameters> {

    override fun preProcessing(logicParameters: ILogicParameters): Boolean {
        if (energyComponent.energyStorage.currentEnergy == 0)
            return false

        return true
    }

    override fun processing(logicParameters: ILogicParameters) {
        // produce no more heat than possible
        val producedHeat = (getGeneratedHeat() * TCFoundation.physics.milliHeatPerRf)
            .coerceAtMost(this.heatBuffer.heatCapacity - this.heatBuffer.heat)

        // consume as much energy as possible
        val consumedEnergy = (producedHeat / TCFoundation.physics.milliHeatPerRf)
            .coerceAtMost(this.energyComponent.energyStorage.currentEnergy)

        if (energyComponent.energyStorage.consumeEnergy(consumedEnergy)) {

            // produce the heat available with the consumed energy
            heatBuffer.heat += consumedEnergy * TCFoundation.physics.milliHeatPerRf
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ILogicParameters) {

    }

    /**
     * Calculate how much heat is generated this tick
     */
    private fun getGeneratedHeat(): Int {
        return (this.baseEnergyConsumption * speedMultiplierComponent.getCappedMultiplier()).toInt()
    }
}