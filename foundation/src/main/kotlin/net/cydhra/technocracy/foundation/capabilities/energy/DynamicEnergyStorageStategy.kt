package net.cydhra.technocracy.foundation.capabilities.energy

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.energy.IEnergyStorage

object DynamicEnergyStorageStategy {

    private const val KEY_CAPACITY = "capacity"
    private const val KEY_MAX_RECEIVE = "limit_receive"
    private const val KEY_MAX_EXTRACT = "limit_extract"
    private const val KEY_CURRENT_AMOUNT = "amount"

    fun readNBT(instance: IEnergyStorage, nbt: NBTBase) {
        with(instance as DynamicEnergyStorage) {
            capacity = (nbt as NBTTagCompound).getInteger(KEY_CAPACITY)
            extractionLimit = nbt.getInteger(KEY_MAX_EXTRACT)
            receivingLimit = nbt.getInteger(KEY_MAX_RECEIVE)
            currentEnergy = nbt.getInteger(KEY_CURRENT_AMOUNT)
        }
    }

    fun writeNBT(instance: IEnergyStorage): NBTBase {
        return NBTTagCompound().apply {
            setInteger(KEY_CAPACITY, instance.maxEnergyStored)
            setInteger(KEY_MAX_EXTRACT, (instance as DynamicEnergyStorage).extractionLimit)
            setInteger(KEY_MAX_RECEIVE, instance.receivingLimit)
            setInteger(KEY_CURRENT_AMOUNT, instance.energyStored)
        }
    }

}