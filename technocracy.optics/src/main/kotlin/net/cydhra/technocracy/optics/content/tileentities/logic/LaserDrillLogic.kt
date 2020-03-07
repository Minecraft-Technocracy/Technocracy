package net.cydhra.technocracy.optics.content.tileentities.logic

import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic

class LaserDrillLogic(
        private val progressComponent: ProgressTileEntityComponent,
        private val energyComponent: EnergyStorageTileEntityComponent,
        private val outputInventory: InventoryTileEntityComponent,
        private val energyPerProgress: Int
) : ILogic {

    override fun preProcessing(): Boolean {
        return energyComponent.energyStorage.currentEnergy > energyPerProgress
    }

    override fun processing() {
        val energyAvailable = energyComponent.energyStorage.currentEnergy
        val progress = energyAvailable / energyPerProgress

        if (energyComponent.energyStorage.consumeEnergy(progress * energyPerProgress)) {
            this.progressComponent.progress += progress
        }

        if (this.progressComponent.progress > 100) {

        }
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }
}