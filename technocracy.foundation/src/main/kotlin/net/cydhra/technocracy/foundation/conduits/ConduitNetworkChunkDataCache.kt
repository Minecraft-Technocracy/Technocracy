package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.util.math.ChunkPos

/**
 * Since chunks are read from disk before they are actually loaded, loaded data must be cached before the chunk
 * actually is added to its [ConduitNetworkDimension]. It is assumed, that a chunk is never read from memory without
 * it being loaded shortly after. If this invariant is broken, a memory leak will occur. Certain warnings may
 * indicate this invariant to be broken, but there is no definite check for the validity of this invariant. When a
 * chunk is being loaded, its data should be removed from this cache. If a chunk is being loaded and there is no data
 * of it in this cache, the chunk has just been generated and does not have any conduit network data assigned.
 */
internal object ConduitNetworkChunkDataCache {

    private val chunkDataCache = mutableMapOf<ChunkPos, ConduitNetworkChunk>()

    /**
     * Adds a set of chunk data to the cache queue, so it can be retrieved when the chunk is actually loaded. If the
     * chunk is read from memory but never loaded, this will leak memory. If data is read twice without the chunk
     * being loaded in between, a warning about the occurring leak is printed.
     *
     * @param chunkPos the position of the chunk whose data is being read
     * @param conduitNetworkChunk the conduit network data read from disk
     */
    internal fun enqueueChunkData(chunkPos: ChunkPos, conduitNetworkChunk: ConduitNetworkChunk) {
        if (chunkDataCache.containsKey(chunkPos)) {
            TCFoundation.logger.warn("A chunk has been read from memory twice without a load event in between. This " +
                    "breaks an assumption about chunk loading mechanisms and can lead to memory leaks. Please report " +
                    "that to the team of technocracy.")
        }

        chunkDataCache[chunkPos] = conduitNetworkChunk
    }

    /**
     * Removes and returns conduit network chunk data from the cache. If no data about the given chunk is available,
     * it has not been read from disk and thus has just been created.
     *
     * @param chunkPos the chunk position of the chunk being loaded
     *
     * @return the enqueued data about the loaded chunk if any has been read before, or null if none was read.
     */
    internal fun popChunkData(chunkPos: ChunkPos): ConduitNetworkChunk? {
        return chunkDataCache.remove(chunkPos)
    }
}