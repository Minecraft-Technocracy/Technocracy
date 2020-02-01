package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeContent

/**
 * Different strategies to find sinks within the network. The function `findSinks` returns a list of zero or more
 * sinks that have to be provided with resources in order of appearance. How many of the sinks the caller wants to
 * supply is at its discretion.
 */
interface RoutingStrategy {

    /**
     * Find a list of sinks that will be provided with resources in order
     *
     * @param source the source transit
     * @param content the content to deliver
     *
     * @return an ordered list of potential targets for the [content]
     */
    fun findSinks(source: TransitSink, content: PipeContent): List<TransitSink>
}