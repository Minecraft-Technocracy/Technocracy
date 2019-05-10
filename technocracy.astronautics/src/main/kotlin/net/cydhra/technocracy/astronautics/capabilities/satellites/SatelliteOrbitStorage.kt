package net.cydhra.technocracy.astronautics.capabilities.satellites

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Basic storage implementation for satellites, that simply gets all satellites of an orbit and serializes them to a
 * list
 */
class SatelliteOrbitStorage : Capability.IStorage<ISatelliteOrbit> {

    override fun readNBT(capability: Capability<ISatelliteOrbit>, instance: ISatelliteOrbit, side: EnumFacing?,
                         nbt: NBTBase) {
        TODO("not implemented: how to deserialize different satellite types? factory?")
    }

    /**
     * Store all satellites of the given instance in a list and return it. Each satellite is stored in a compound tag
     */
    override fun writeNBT(capability: Capability<ISatelliteOrbit>, instance: ISatelliteOrbit, side: EnumFacing?):
            NBTBase {
        val list = NBTTagList()

        instance.getSatellites().forEach { satellite ->
            val satelliteCompound = NBTTagCompound()
            satellite.serializeNbt(satelliteCompound)
            list.appendTag(satelliteCompound)
        }

        return list
    }

}