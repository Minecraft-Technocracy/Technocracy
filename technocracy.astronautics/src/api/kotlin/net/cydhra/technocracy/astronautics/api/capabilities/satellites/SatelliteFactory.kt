package net.cydhra.technocracy.astronautics.api.capabilities.satellites

/**
 * A registry for all satellite types where constructors are registered, so the deserialization of
 * [SatelliteOrbitStorage] can create instances of the satellites and use them.
 */
object SatelliteFactory {

    private val registeredSatelliteTypes = mutableMapOf<String, () -> ISatellite>()

    /**
     * Register a satellite type at the factory. Registered types can be deserialized from chunks, unregistered types
     * will fail deserialization
     *
     * @param name unique name of the satellite type.
     * @param constructor default constructor to create an empty instance of the satellite type to be deserialized
     */
    fun <T : ISatellite> registerSatelliteType(name: String, constructor: () -> T) {
        registeredSatelliteTypes += name to constructor
    }

    /**
     * Create an instance of a given satellite type that can then be filled with data from deserialization
     *
     * @param name satellite type
     *
     * @return an instance of the registered type or null, if no such type is registered
     */
    fun createSatellite(name: String): ISatellite? {
        return this.registeredSatelliteTypes[name]?.invoke()
    }
}