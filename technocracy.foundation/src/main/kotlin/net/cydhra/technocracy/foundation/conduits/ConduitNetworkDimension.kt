package net.cydhra.technocracy.foundation.conduits

import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.world.ChunkDataEvent

internal class ConduitNetworkDimension(private val dimensionId: Int) {

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
        // TODO saving

        markedForRemoval.remove(event.chunk.pos)
    }

    /**
     * Read conduit data from provided NBT data and apply it to a new instance of [ConduitNetworkChunk]. The chunk is
     * then pushed into the chunk data cache, so it can be retrieved whenever the chunk is actually loaded through
     * [loadChunk]
     */
    fun loadChunkData(event: ChunkDataEvent.Load) {
        val chunkData = ConduitNetworkChunk(event.chunk.pos)
        // TODO loading

        ConduitNetworkChunkDataCache.enqueueChunkData(event.chunk.pos, chunkData)
    }
}