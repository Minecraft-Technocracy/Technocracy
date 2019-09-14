package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink

object GreedyRouting : RoutingStrategy<GreedyQuery> {
    override fun query(providerSink: TransitSink): GreedyQuery {
        TODO("not implemented")
    }

    override fun provideTargetSink(route: GreedyQuery): TransitSink {
        TODO("not implemented")
    }
}

class GreedyQuery(providerSink: TransitSink) : NetworkQuery(providerSink) {

}