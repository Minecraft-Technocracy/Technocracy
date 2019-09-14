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
        TODO("not implemented")
    }

    override fun transferAsset(query: GreedyQuery, transferAsset: TransferAsset) {
        TODO("not implemented")
    }
}

class GreedyQuery(providerSink: TransitSink) : NetworkQuery(providerSink) {
    val routes = HashMap<TransferAsset, TransitSink>()
}