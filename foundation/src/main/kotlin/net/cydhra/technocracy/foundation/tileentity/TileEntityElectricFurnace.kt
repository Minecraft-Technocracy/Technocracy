package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage


class TileEntityElectricFurnace : AbstractMachine(energyStorage = DynamicEnergyStorage(capacity = 5000, extractionLimit = 0)) {
    override fun update() {
    }
}