package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.world.ChunkDataEvent

internal class ConduitNetworkDimension(private val dimensionId: Int) {

    companion object {
        private const val NBT_KEY_NETWORK_DATA = "conduits"
    }

    /**
     * A map of all chunks currently loaded
     */
    private val loadedChunks: MutableMap<ChunkPos, ConduitNetworkChunk> = mutableMapOf()

    /**
     * A map of chunks marked for removal
     */
    private val markedForRemoval: MutableMap<ChunkPos, ConduitNetworkChunk> = mutableMapOf()


    /**
     * Called when a chunk loads so the chunk can be parsed and inserted into the dimension network.
     *
     * @param chunk the newly loaded chunk
     */
    internal fun loadChunk(chunk: Chunk) {
        loadedChunks[chunk.pos] = ConduitNetworkChunkDataCache.popChunkData(chunk.pos) ?: ConduitNetworkChunk(chunk.pos)
    }

    /**
     * Called when a chunk is unloaded, so the chunk can be removed from the dimension network. Note, that the
     * [ConduitNetworkChunk] does not necessarily goes out of scope immediately.
     *
     * @param chunk the chunk that is being unloaded
     */
    internal fun unloadChunk(chunk: Chunk) {
        markedForRemoval[chunk.pos] = loadedChunks.remove(chunk.pos)!!
    }

    /**
     * @param pos a chunk position within this dimension
     *
     * @return the chunk at the given position or null, if that chunk is not loaded
     */
    internal fun getChunkAt(pos: ChunkPos): ConduitNetworkChunk? {
        return loadedChunks[pos]
    }

    /**
     * Save all conduit data to the chunk's NBT data
     */
    fun saveChunkData(event: ChunkDataEvent.Save) {
        val chunk = markedForRemoval.remove(event.chunk.pos) ?: this.getChunkAt(event.chunk.pos)!!
        event.data.setTag(NBT_KEY_NETWORK_DATA, chunk.serializeNBT())
    }

    /**
     * Read conduit data from provided NBT data and apply it to a new instance of [ConduitNetworkChunk]. The chunk is
     * then pushed into the chunk data cache, so it can be retrieved whenever the chunk is actually loaded through
     * [loadChunk]
     */
    fun loadChunkData(event: ChunkDataEvent.Load) {
        val chunkData = ConduitNetworkChunk(event.chunk.pos)
        chunkData.deserializeNBT(event.data.getCompoundTag(NBT_KEY_NETWORK_DATA))

        ConduitNetworkChunkDataCache.enqueueChunkData(event.chunk.pos, chunkData)
    }


    internal fun debug_getChunks(): Collection<ConduitNetworkChunk> {
        return this.loadedChunks.values
    }

    /**
     * Tick the network within this dimension.
     */
    fun tick(world: WorldServer) {
        val transitGraph = this.loadedChunks
                .map { (_, chunk) -> chunk.getTransitEdges() }
                .flatten()
                .map { Pair(it.id, it) }
                .toMap()

        transitGraph.values
                .filterIsInstance<TransitSink>()
                .forEach(TransitSink::tick)

        transitGraph.values
                .filterIsInstance<TransitSink>()
                .filter { it.offersContent(world) }
                .forEach { source ->
                    // get offered content of source
                    val content = source.getContent(world)

                    // find available sink using routing strategy
                    val targets = source.routingStrategy.findSinks(source, content)

                    // transfer content

                }
    }
}