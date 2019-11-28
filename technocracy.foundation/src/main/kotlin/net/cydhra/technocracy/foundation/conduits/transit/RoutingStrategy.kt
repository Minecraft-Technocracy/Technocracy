package net.cydhra.technocracy.foundation.conduits.transit

interface RoutingStrategy {

    fun findSink(content: Any): TransitSink
}