package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink

/**
 * The simplest routing strategy. It will always transfer items to the closest available sink.
 */
object GreedyRouting : RoutingStrategy<GreedyQuery> {
    override fun prepareQuery(providerSink: TransitSink): GreedyQuery {
        return GreedyQuery(providerSink)
    }

    override fun canTransfer(query: GreedyQuery, transferAsset: TransferAsset): Boolean {
        if (query.routes.containsKey(transferAsset)) {
            // verify that the target still accepts more of the asset, and if so, return true
            if (transferAsset.acceptsAsset(query.routes[transferAsset]!!.target)) {
                return true
            } else {
                query.routes.remove(transferAsset)
            }
        }

        // routing for asset

        // if route was found, return true, otherwise false
        TODO()
    }

    override fun transferAsset(query: GreedyQuery, transferAsset: TransferAsset) {
        val route = query.routes[transferAsset]
                ?: throw IllegalStateException("No route for asset was found, but `transferAsset` was called")

        transferAsset.performTransfer(query.providerSink, route.target)
    }
}

/**
 * Routing state used by [GreedyRouting]. Do not tamper with the contents of instances of this class.
 */
class GreedyQuery(providerSink: TransitSink) : NetworkQuery(providerSink) {
    // theoretically it would be useful to hide the contents of this class from the world outside of the routing
    // strategy, but kotlin does not offer a mechanism for that, so whatever

    /**
     * All cached routes by their asset type. Since everything is greedily routed, only one route per type is required
     */
    val routes = HashMap<TransferAsset, Route>()

    /**
     *
     */
    data class Route(val target: TransitSink, val maxQuantity: Int)
}
