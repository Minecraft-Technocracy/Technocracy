package net.cydhra.technocracy.astronautics.api.capabilities.satellites

import net.minecraft.world.chunk.Chunk

/**
 * Default implementation for a satellite orbit
 */
class SafeSatelliteOrbit(private val chunk: Chunk) : ISatelliteOrbit {
    /**
     * List of satellites in this orbit
     */
    private val registeredSatellites = mutableListOf<ISatellite>()

    /**
     * Add a satellite to the list of registered satellites.
     *
     * @param markDirty whether to mark the chunk dirty
     */
    override fun add(satellite: ISatellite, markDirty: Boolean) {
        registeredSatellites += satellite

        if (markDirty) {
            chunk.markDirty()
        }
    }

    override fun getSatellites(): List<ISatellite> {
        return registeredSatellites
    }

    override fun tick() {
        this.registeredSatellites.forEach(ISatellite::tick)
    }

}