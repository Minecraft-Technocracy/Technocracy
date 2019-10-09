package net.cydhra.technocracy.foundation.capabilities.energy

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.energy.IEnergyStorage

class EnergyCapabilityProvider(private val instance: DynamicEnergyCapability) : ICapabilitySerializable<NBTBase> {
    companion object {
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
        DynamicEnergyStorageStrategy.readNBT(this.instance, nbt)
    }

    override fun serializeNBT(): NBTBase {
        return DynamicEnergyStorageStrategy.writeNBT(this.instance)
    }
}