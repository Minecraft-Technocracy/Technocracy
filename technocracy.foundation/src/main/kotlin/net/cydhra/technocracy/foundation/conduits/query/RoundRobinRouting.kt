package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink


object RoundRobinRouting : RoutingStrategy<RRQuery> {
    override fun prepareQuery(providerSink: TransitSink): RRQuery {
        TODO("not implemented")
    }

    override fun canTransfer(query: RRQuery, transferAsset: TransferAsset): Boolean {
        TODO("not implemented")
    }

    override fun transferAsset(query: RRQuery, transferAsset: TransferAsset) {
        TODO("not implemented")
    }
}

class RRQuery(providerSink: TransitSink) : NetworkQuery(providerSink)