package net.cydhra.technocracy.foundation.capabilities.energy

import net.minecraftforge.energy.IEnergyStorage

/**
 * A implementation of [IEnergyStorage] that can be dynamically adjusted in its limits
 */
class DynamicEnergyStorage(currentEnergy: Int = 0, capacity: Int,
                           var extractionLimit: Int, var receivingLimit: Int = -1) : IEnergyStorage {

    var currentEnergy: Int = currentEnergy
        private set

    var capacity: Int = capacity
        set(value) {
            this.currentEnergy = Math.min(this.currentEnergy, value)
            field = value
        }

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
        val totalReceived = Math.min(Math.min(this.capacity - this.currentEnergy, maxReceive), if (this.receivingLimit != -1) this.receivingLimit else Integer.MAX_VALUE)

        if (!simulate)
            this.currentEnergy += totalReceived

        return totalReceived
    }

    override fun canReceive(): Boolean {
        return receivingLimit > 0 || receivingLimit == -1
    }

    /**
     * Consumes energy of the storage for internal machine purposes like item processing. Returns true, if the
     * consumption was successful, false if not. Does not respect extraction limit.
     *
     * @param amount the amount of energy to consume
     */
    fun consumeEnergy(amount: Int): Boolean {
        if (this.currentEnergy >= amount) {
            this.currentEnergy -= amount
            return true
        }

        return false
    }

    /**
     * Force an unchecked update of the internal energy storage. This should only be done by the serializer and not
     * by machine logic.
     */
    fun forceUpdateOfCurrentEnergy(currentEnergy: Int) {
        this.currentEnergy = currentEnergy
    }
}