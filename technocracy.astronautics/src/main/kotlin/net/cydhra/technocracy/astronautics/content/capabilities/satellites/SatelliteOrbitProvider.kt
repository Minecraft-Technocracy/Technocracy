package net.cydhra.technocracy.astronautics.content.capabilities.satellites

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable

/**
 * Provides the satellite orbit capability for chunks.
 */
class SatelliteOrbitProvider(chunk: Chunk) : ICapabilitySerializable<NBTBase> {

    private val satelliteOrbit = SafeSatelliteOrbit(chunk)

    companion object {
        @JvmStatic
        @CapabilityInject(ISatelliteOrbit::class)
        lateinit var CAPABILITY_SATELLITE_ORBIT: Capability<ISatelliteOrbit>
    }

    override fun serializeNBT(): NBTBase? {
        return CAPABILITY_SATELLITE_ORBIT.storage.writeNBT(CAPABILITY_SATELLITE_ORBIT, satelliteOrbit, null)
    }

    override fun deserializeNBT(nbt: NBTBase?) {
        CAPABILITY_SATELLITE_ORBIT.storage.readNBT(CAPABILITY_SATELLITE_ORBIT, satelliteOrbit, null, nbt)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY_SATELLITE_ORBIT
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CAPABILITY_SATELLITE_ORBIT) CAPABILITY_SATELLITE_ORBIT.cast(this.satelliteOrbit) else null
    }

}
