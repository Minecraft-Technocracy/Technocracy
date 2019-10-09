package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyCapability
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorageStrategy
import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * A machine component that handles energy storage and transfer speeds of the machine. The storage defaults to a
 * capacity of 8000 and a receiving limit of 8000 FE. If a machine wants to change the defaults, it has to do so in
 * its constructor.
 */
class EnergyStorageComponent(val facing: MutableSet<EnumFacing>) : AbstractCapabilityComponent() {

    override val type: ComponentType = ComponentType.ENERGY

    /**
     * The energy storage capability instance containing energy storage state
     */
    val energyStorage: DynamicEnergyCapability = DynamicEnergyCapability(
            capacity = 8000,
            currentEnergy = 0,
            extractionLimit = 0,
            receivingLimit = 8000)

    init {
        energyStorage.componentParent = this
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == EnergyCapabilityProvider.CAPABILITY_ENERGY && this.facing.contains(facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return EnergyCapabilityProvider.CAPABILITY_ENERGY!!.cast(this.energyStorage)
    }

    override fun serializeNBT(): NBTTagCompound {
        return DynamicEnergyStorageStrategy.writeNBT(this.energyStorage) as NBTTagCompound
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        DynamicEnergyStorageStrategy.readNBT(this.energyStorage, nbt)
    }
}