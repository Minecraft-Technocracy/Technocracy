package net.cydhra.technocracy.astronautics.capabilities.satellites

/**
 * Default implementation for a satellite orbit
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

}