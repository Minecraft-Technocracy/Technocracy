package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage


class TileEntityPulveriser : AbstractMachine(DynamicEnergyStorage(capacity = 100, currentEnergy = 100, extractionLimit = 100, receivingLimit = 100)) {
    override fun update() {

    }
}