package net.cydhra.technocracy.foundation.conduits

import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.Chunk

internal class ConduitNetworkDimension(private val dimensionId: Int) {

    /**
     * A map of all chunks currently loaded
     */
    private val loadedChunks: MutableMap<ChunkPos, ConduitNetworkChunk> = mutableMapOf()

    /**
     * Called when a chunk loads so the chunk can be parsed and inserted into the dimension network.
     *
     * @param chunk the newly loaded chunk
     */
    internal fun loadChunk(chunk: Chunk) {
        println("load: ${chunk.pos}")
    }

    /**
     * Called when a chunk is unloaded, so the chunk can be removed from the dimension network. Note, that the
     * [ConduitNetworkChunk] does not necessarily goes out of scope immediately.
     *
     * @param chunk the chunk that is being unloaded
     *
     * @return true, if no more chunks are loaded in this dimension, false otherwise
     */
    internal fun unloadChunk(chunk: Chunk): Boolean {
        println("unload: ${chunk.pos}")
        return false
    }

    /**
     * @param pos a chunk position within this dimension
     *
     * @return the chunk at the given position or null, if that chunk is not loaded
     */
    internal fun getChunkAt(pos: ChunkPos): ConduitNetworkChunk? {
        return loadedChunks[pos]
    }
}