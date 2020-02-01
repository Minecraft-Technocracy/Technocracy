package net.cydhra.technocracy.foundation.conduits.transit

interface RoutingStrategy {

    fun findSinks(content: Any): TransitSink
}