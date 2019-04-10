package net.cydhra.technocracy.foundation.capabilities.energy

import net.minecraftforge.energy.IEnergyStorage

/**
 * A implementation of [IEnergyStorage] that can be dynamically adjusted in its limits
 */
class DynamicEnergyStorage(var currentEnergy: Int = 0, var capacity: Int,
                           var extractionLimit: Int, var receivingLimit: Int) : IEnergyStorage {

    override fun canExtract(): Boolean {
        return extractionLimit > 0
    }

    override fun getMaxEnergyStored(): Int {
        return capacity
    }

    override fun getEnergyStored(): Int {
        return currentEnergy
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        val totalExtracted = Math.min(Math.min(this.currentEnergy, maxExtract), this.extractionLimit)

        if (!simulate)
            this.currentEnergy -= totalExtracted

        return totalExtracted
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        val totalReceived = Math.min(Math.min(this.capacity - this.currentEnergy, maxReceive), this.receivingLimit)

        if (!simulate)
            this.currentEnergy += totalReceived

        return totalReceived
    }

    override fun canReceive(): Boolean {
        return receivingLimit > 0
    }
}