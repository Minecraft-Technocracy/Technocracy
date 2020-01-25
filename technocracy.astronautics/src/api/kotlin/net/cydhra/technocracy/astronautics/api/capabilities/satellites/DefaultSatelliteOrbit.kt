package net.cydhra.technocracy.astronautics.api.capabilities.satellites

/**
 * Default implementation for a satellite orbit that does not mark modified chunks as dirty.
 */
class DefaultSatelliteOrbit : ISatelliteOrbit {
    /**
     * List of satellites in this orbit
     */
    private val registeredSatellites = mutableListOf<ISatellite>()

    /**
     * Add a satellite to the list of registered satellites. Mark dirty is unsupported in the default implementation
     */
    override fun add(satellite: ISatellite, markDirty: Boolean) {
        registeredSatellites += satellite
    }

    override fun getSatellites(): List<ISatellite> {
        return registeredSatellites
    }

    override fun tick() {
        this.registeredSatellites.forEach(ISatellite::tick)
    }
}
