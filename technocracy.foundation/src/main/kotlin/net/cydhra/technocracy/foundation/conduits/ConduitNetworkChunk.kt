package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer

/**
 * A model for a chunk of the conduit network. Can be removed from the conduit network dimension without breaking
 * routing algorithms. Note, that this model does never save the chunk directly to prevent memory leaks, as the
 * [ConduitNetworkChunk] instance may remain in memory even after the chunk was unloaded.
 *
 * @param chunkPos the chunk position in the dimension
 */
internal class ConduitNetworkChunk(private val chunkPos: ChunkPos) {

    /**
     * A counter that gets increased, every time the conduit network within this chunk is modified. Checking this
     * counter against a previously saved value can verify that the chunk's network is unchanged.
     */
    internal var cacheValidationCounter: Int = 0
        private set

    /**
     * Add a node to the conduit network. This method does only add this one node to the network: no additional nodes
     * are discovered in the neighborhood of the block. If the node already exists, an [IllegalStateException] is
     * thrown. No edges are inserted into the network. It is asserted that [pos] is within the chunk modeled by this
     * instance and [world] is the owner of that chunk.
     *
     * @param world world server that adds the node
     * @param pos position in world where the node is added
     * @param type pipe type of the new node. Each node does only have one pipe type. Multiple pipe types within the
     * same block must be added individually.
     *
     * @throws IllegalStateException if the node already exists
     */
    internal fun insertNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Remove a node from the conduit network. All edges from and to the node are removed as well. If the node does
     * not exist, an [IllegalStateException] is thrown. It is asserted that [pos] is within the chunk modeled by this
     * instance and [world] is the owner of that chunk.
     *
     * @param world world server that removes the node
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     */
    internal fun removeNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Insert an edge into the conduit network. The edge is constructed between the two given nodes and has no
     * direction. Therefore the order of nodes does not matter. The edge is constructed for the given pipe [type]. Only
     * one edge is inserted. If the edge already exists, an [IllegalStateException] is thrown. The node positions
     * given must be adjacent and both contain a node of given [type]. It is asserted that the positions are within the
     * chunk modeled by this instance and [world] is the owner of that chunk.
     *
     * @param world world server that inserts the edge
     * @param nodeA first end of the new edge
     * @param nodeB second end of the new edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalArgumentException] if one of the node positions does not contain a [type] node
     * @throws [IllegalStateException] if the edge already exists
     */
    internal fun insertEdge(world: WorldServer, nodeA: BlockPos, nodeB: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Remove an edge from the conduit network. The edge must exist, otherwise an [IllegalStateException] is thrown.
     * No further nodes or edges are removed. It is asserted that the positions are within the
     * chunk modeled by this instance and [world] is the owner of that chunk.
     *
     * @param world world server that removes the edge
     * @param nodeA first end of the new edge
     * @param nodeB second end of the new edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalStateException] if the edge does not exist
     */
    internal fun removeEdge(world: WorldServer, nodeA: BlockPos, nodeB: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Mark this chunk as dirty
     */
    private fun markDirty() {
        cacheValidationCounter++
    }
}