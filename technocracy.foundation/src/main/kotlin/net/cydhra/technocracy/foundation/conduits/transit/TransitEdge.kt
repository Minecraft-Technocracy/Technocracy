package net.cydhra.technocracy.foundation.conduits.transit

/**
 * A edge in the transit-network that is a temporary compression of the conduit network. The transit network It is used
 * to efficiently traverse the conduit network, skipping nodes that do not do anything within the network except
 * connecting other nodes. It is also used to efficiently insert and remove chunks from the conduit network while
 * maintaining sound functionality.
 * Note, that there are no transit nodes, as the only relevant part that is modelled by the transit network are
 * connections between chunks and connections between the network and its sinks.
 *
 * @see [TransitSink]
 */
open class TransitEdge {

}