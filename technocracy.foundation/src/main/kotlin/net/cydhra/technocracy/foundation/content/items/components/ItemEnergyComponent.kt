package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicEnergyCapability
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicEnergyStorageStrategy
import net.cydhra.technocracy.foundation.content.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.model.items.capability.AbstractItemCapabilityComponent
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability


class ItemEnergyComponent(var capacity: Int = 8000) : AbstractItemCapabilityComponent() {
    override val type = ComponentType.ENERGY

    /**
     * The energy storage capability instance containing energy storage state
     */
    val energyStorage: DynamicEnergyCapability = DynamicEnergyCapability(
            capacity = capacity,
            currentEnergy = 0,
            extractionLimit = 0,
            receivingLimit = capacity)

    init {
        energyStorage.componentParent = this
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == EnergyCapabilityProvider.CAPABILITY_ENERGY
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