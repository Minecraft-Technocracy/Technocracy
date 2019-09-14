package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink

object RoundRobinRouting : RoutingStrategy<RRQuery> {
    override fun query(providerSink: TransitSink): RRQuery {
        TODO("not implemented")
    }

    override fun provideTargetSink(route: RRQuery): TransitSink {
        TODO("not implemented")
    }

}

class RRQuery(providerSink: TransitSink) : NetworkQuery(providerSink)