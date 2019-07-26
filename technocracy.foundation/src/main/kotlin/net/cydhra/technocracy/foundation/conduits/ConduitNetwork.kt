package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Global facade to the conduit network. All components that interact with the conduit network shall talk to this
 * opaque facade. No implementation details about the network and routing shall be visible. Interaction shall happen
 * in a declarative way (i.e. adding a node by its position and type, not by any wrapper object instance)
 */
object ConduitNetwork {

    /**
     * A map of all dimensions that have at least one chunk loaded.
     */
    private val dimensions: MutableMap<Int, ConduitNetworkDimension> = mutableMapOf()

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
     * @throws IllegalStateException if the respective chunk is not loaded
     */
    fun addConduitNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.insertNode(world, pos, type)
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
     * @throws IllegalStateException if the respective chunk is not loaded
     */
    fun removeConduitNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.removeNode(world, pos, type)
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

    /**
     * When a chunk is read from NBT, it has been loaded before and might have conduit data written to it. That must
     * be retrieved
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkDataLoad(event: ChunkDataEvent.Load) {
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId]!!

        dimension.loadChunkData(event)
    }

    /**
     * When a chunk is being saved to NBT, but does not exist within the network, it is being generated. We can add
     * it to the list of loaded chunks as it is being loaded obviously.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkDataSave(event: ChunkDataEvent.Save) {
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId]!!

        dimension.saveChunkData(event)
    }

    /**
     * When a chunk gets loaded, it must be added to the dimension network
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkLoad(event: ChunkEvent.Load) {
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId] ?: ConduitNetworkDimension(dimensionId).apply {
            dimensions[dimensionId] = this
        }

        dimension.loadChunk(event.chunk)
    }

    /**
     * When a chunk gets unloaded, remove it from the dimension.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkUnload(event: ChunkEvent.Unload) {
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId]!!

        dimension.unloadChunk(event.chunk)
    }
}