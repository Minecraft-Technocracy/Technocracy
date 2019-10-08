package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.minecraft.util.math.ChunkPos

/**
 * All searches and routes within a conduit network are represented through this model. It can be used to cache
 * results of path finding and sink discovery. Cached queries' validity can be checked and if they are invalid, a
 * chunk that is affected by the query has been changed. If a chunk is updated, its validation counter increases,
 * indicating to cached queries, that it has been changed. Chunk unloading is checked against the
 * [net.cydhra.technocracy.foundation.conduits.ConduitNetwork].
 * A single [NetworkQuery] instance may contain multiple routes for different [TransferAsset]s, as a found route may
 * be cached while transferring a different asset and results about unavailable routes may be cached as well.
 * Depending on the routing strategy, even more available routes may be cached here. If any of the routes becomes
 * invalid, all cached routes are marked as invalid, though.
 * TODO: find a way to invalidate a query if a chunk containing reachable network parts is loaded, that is, a chunk
 *  next to an already watched chunk is suddenly available and offers sinks that might be preferable within the
 *  current strategy.
 */
open class NetworkQuery(val providerSink: TransitSink) {

    /**
     * A list of chunks affected by this query. If any of them updates, this query must be invalid.
     */
    private val affectedChunks: MutableSet<ChunkPos> = mutableSetOf()

    /**
     * A list of chunks that are technically reachable from this network but are currently not loaded. If any of them
     * is loaded, the query must be invalid.
     */
    private val reachableUnavailableChunks: MutableSet<ChunkPos> = mutableSetOf()

    /**
     * Check wether all cached routes are still valid. If any of them becomes invalid, false is returned and this
     * instance may not be used again.
     */
    fun isValid(): Boolean {
        TODO()
    }
}