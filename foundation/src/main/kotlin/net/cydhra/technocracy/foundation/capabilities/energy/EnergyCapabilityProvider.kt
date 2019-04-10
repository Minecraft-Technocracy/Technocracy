package net.cydhra.technocracy.foundation.capabilities.energy

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.energy.IEnergyStorage

class EnergyCapabilityProvider(private val instance: DynamicEnergyStorage) : ICapabilitySerializable<NBTBase> {
    companion object {
        private const val KEY_CAPACITY = "capacity"
        private const val KEY_MAX_RECEIVE = "limit_receive"
        private const val KEY_MAX_EXTRACT = "limit_extract"
        private const val KEY_CURRENT_AMOUNT = "amount"

        @JvmStatic
        @CapabilityInject(IEnergyStorage::class)
        var CAPABILITY_ENERGY: Capability<IEnergyStorage>? = null
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CAPABILITY_ENERGY)
            CAPABILITY_ENERGY!!.cast<T>(this.instance)
        else
            null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY_ENERGY
    }

    override fun deserializeNBT(nbt: NBTBase) {
        with(instance) {
            capacity = (nbt as NBTTagCompound).getInteger(KEY_CAPACITY)
            extractionLimit = nbt.getInteger(KEY_MAX_EXTRACT)
            receivingLimit = nbt.getInteger(KEY_MAX_RECEIVE)
            currentEnergy = nbt.getInteger(KEY_CURRENT_AMOUNT)
        }
    }

    override fun serializeNBT(): NBTBase {
        return NBTTagCompound().apply {
            setInteger(KEY_CAPACITY, instance.maxEnergyStored)
            setInteger(KEY_MAX_EXTRACT, instance.extractionLimit)
            setInteger(KEY_MAX_RECEIVE, instance.receivingLimit)
            setInteger(KEY_CURRENT_AMOUNT, instance.energyStored)
        }
    }
}