package net.cydhra.technocracy.astronautics.api.capabilities.satellites

import net.cydhra.technocracy.astronautics.TCAstronautics
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

    companion object {
        const val NBT_KEY_TYPE = "type"
    }

    override fun readNBT(capability: Capability<ISatelliteOrbit>, instance: ISatelliteOrbit, side: EnumFacing?,
                         nbt: NBTBase) {
        if (nbt is NBTTagList) {
            nbt.forEach { element ->
                if (element is NBTTagCompound) {
                    if (!element.hasKey(NBT_KEY_TYPE))
                        error("expected satellite type in compound")

                    val type = element.getString(NBT_KEY_TYPE)
                    val satellite = SatelliteFactory.createSatellite(type)

                    if (satellite == null) {
                        TCAstronautics.logger.warn("satellite of type $type could not be deserialized. Removing it " +
                                "from chunk")
                    } else {
                        try {
                            satellite.deserializeNbt(element)
                        } catch (t: Throwable) {
                            TCAstronautics.logger.error("error while deserializing satellite ($type)", t)
                        }

                        capability.defaultInstance!!.add(satellite, false)
                    }
                } else {
                    error("wrong nbt tag for satellite. Expected compound but got ${element.javaClass.name}")
                }
            }
        } else {
            error("wrong NBT tag for satellite orbit storage. Expected list but got ${nbt.javaClass.name}")
        }
    }

    /**
     * Store all satellites of the given instance in a list and return it. Each satellite is stored in a compound tag
     */
    override fun writeNBT(capability: Capability<ISatelliteOrbit>, instance: ISatelliteOrbit, side: EnumFacing?):
            NBTBase {
        val list = NBTTagList()

        instance.getSatellites().forEach { satellite ->
            val satelliteCompound = NBTTagCompound()
            satelliteCompound.setString(NBT_KEY_TYPE, satellite.type)
            satellite.serializeNbt(satelliteCompound)
            list.appendTag(satelliteCompound)
        }

        return list
    }

}