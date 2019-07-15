package net.cydhra.technocracy.astronautics.capabilities.satellites

/**
 * Interface for the chunk capability that holds satellite data of a chunk or a world dimension
 */
interface ISatelliteOrbit {

    /**
     * Get a list of all satellites in the chunk's or world's orbit
     */
    fun getSatellites(): List<ISatellite>

    /**
     * Add a satellite to the chunk's or world's orbit
     *
     * @param satellite satellite implementation to shoot into orbit
     * @param markDirty whether to mark the chunk for memory update
     */
    fun add(satellite: ISatellite, markDirty: Boolean)

    /**
     * Tick this orbit
     */
    fun tick()
}