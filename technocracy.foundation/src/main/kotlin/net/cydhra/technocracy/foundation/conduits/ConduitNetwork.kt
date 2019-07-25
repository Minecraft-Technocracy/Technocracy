package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer

/**
 * Global facade to the conduit network. All components that interact with the conduit network shall talk to this
 * opaque facade. No implementation details about the network and routing shall be visible. Interaction shall happen
 * in a declarative way (i.e. adding a node by its position and type, not by any wrapper object instance)
 */
object ConduitNetwork {

    /**
     * Add a node to the conduit network. This method does only add this one node to the network: no additional nodes
     * are discovered in the neighborhood of the block. If the node already exists, an [IllegalStateException] is
     * thrown. No edges are inserted into the network.
     *
     * @param world world server that adds the node
     * @param pos position in world where the node is added
     * @param type pipe type of the new node. Each node does only have one pipe type. Multiple pipe types within the
     * same block must be added individually.
     *
     * @throws IllegalStateException if the node already exists
     */
    fun addConduitNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Remove a node from the conduit network. All edges from and to the node are removed as well. If the node does
     * not exist, an [IllegalStateException] is thrown.
     *
     * @param world world server that removes the node
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     */
    fun removeConduitNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Insert an edge into the conduit network. The edge is constructed between the two given nodes and has no
     * direction. Therefore the order of nodes does not matter. The edge is constructed for the given pipe [type]. Only
     * one edge is inserted. If the edge already exists, an [IllegalStateException] is thrown. The node positions
     * given must be adjacent and both contain a node of given [type].
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
    fun insertConduitEdge(world: WorldServer, nodeA: BlockPos, nodeB: BlockPos, type: PipeType) {
        TODO("not implemented")
    }

    /**
     * Remove an edge from the conduit network. The edge must exist, otherwise an [IllegalStateException] is thrown.
     * No further nodes or edges are removed.
     *
     * @param world world server that removes the edge
     * @param nodeA first end of the new edge
     * @param nodeB second end of the new edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalStateException] if the edge does not exist
     */
    fun removeConduitEdge(world: WorldServer, nodeA: BlockPos, nodeB: BlockPos, type: PipeType) {
        TODO("not implemented")
    }
}