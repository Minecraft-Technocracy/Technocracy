package net.cydhra.technocracy.foundation.conduits

import net.minecraft.world.chunk.Chunk

internal class ConduitNetworkDimension(private val dimensionId: Int) {

    /**
     * Called when a chunk loads so the chunk can be parsed and inserted into the dimension network.
     *
     * @param chunk the newly loaded chunk
     */
    internal fun loadChunk(chunk: Chunk) {
        TODO("not implemented")
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
        TODO("not implemented")
    }
}