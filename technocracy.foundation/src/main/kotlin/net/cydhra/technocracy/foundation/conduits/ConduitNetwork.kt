package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.conduits.parts.AttachmentPart
import net.cydhra.technocracy.foundation.conduits.parts.EdgePart
import net.cydhra.technocracy.foundation.conduits.parts.NodePart
import net.cydhra.technocracy.foundation.conduits.parts.Part
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.conduits.ServerConduitNetworkSyncPacket
import net.cydhra.technocracy.foundation.network.conduits.ServerConduitNetworkUpdatePacket
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.event.world.ChunkWatchEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
    fun addConduitNode(
        transactionContext: NetworkTransactionContext, world: WorldServer, pos: BlockPos,
        type: PipeType
    ) {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        transactionContext.markPartAdded(chunk, pos, chunk.insertNode(pos, type))
        transactionContext.markChunkModified(world.provider.dimension, chunk)
    }

    /**
     * Remove a node from the conduit network. No edges from and to the node are removed as well. If the node does
     * not exist, an [IllegalStateException] is thrown.
     *
     * @param world world server that removes the node
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     * @throws IllegalStateException if the respective chunk is not loaded
     */
    fun removeConduitNode(
        transactionContext: NetworkTransactionContext, world: WorldServer, pos: BlockPos,
        type: PipeType
    ) {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        transactionContext.markPartsRemoved(chunk, pos, chunk.removeNode(pos, type))
        transactionContext.markChunkModified(world.provider.dimension, chunk)
    }

    /**
     * Insert an edge into the conduit network. The edge is constructed between the two given nodes and has no
     * direction. Therefore the order of nodes does not matter. The edge is constructed for the given pipe [type]. Only
     * one edge is inserted (however it will be inserted in both nodes). If the edge already exists, an
     * [IllegalStateException] is thrown. The node positions given must be adjacent and both contain a node of given
     * [type] and must be adjacent.
     *
     * @param world world server that inserts the edge
     * @param nodeA first end of the new edge
     * @param nodeB second end of the new edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalArgumentException] if one of the node positions does not contain a [type] node
     * @throws [IllegalStateException] if the edge already exists
     * @throws [IllegalStateException] if the dimension is not loaded
     * @throws [IllegalStateException] if one of the chunks is not loaded
     */
    fun insertConduitEdge(
        transactionContext: NetworkTransactionContext, world: WorldServer, nodeA: BlockPos,
        nodeB: BlockPos, type: PipeType
    ) {
        val directionFromA = EnumFacing.values().firstOrNull { nodeA.add(it.directionVec) == nodeB }
            ?: throw IllegalArgumentException("the positions are not adjacent")

        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunkA =
            dimension.getChunkAt(ChunkPos(nodeA)) ?: throw IllegalStateException("the chunk of nodeA is not loaded")
        transactionContext.markPartAdded(chunkA, nodeA, chunkA.insertEdge(nodeA, directionFromA, type))

        val chunkB =
            dimension.getChunkAt(ChunkPos(nodeB)) ?: throw IllegalStateException("the chunk of nodeB is not loaded")
        transactionContext.markPartAdded(chunkB, nodeB, chunkB.insertEdge(nodeB, directionFromA.opposite, type))
        transactionContext.markChunkModified(world.provider.dimension, chunkA)
        transactionContext.markChunkModified(world.provider.dimension, chunkB)
    }

    /**
     * Remove an edge from the conduit network. The edge must exist, otherwise an [IllegalStateException] is thrown.
     * No further nodes or edges are removed (however the edge is removed from both blocks).
     *
     * @param world world server that removes the edge
     * @param nodeA first end of the new edge
     * @param nodeB second end of the new edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalStateException] if the edge does not exist
     */
    fun removeConduitEdge(
        transactionContext: NetworkTransactionContext, world: WorldServer, nodeA: BlockPos,
        nodeB: BlockPos, type: PipeType
    ) {
        val directionFromA = EnumFacing.values().firstOrNull { nodeA.add(it.directionVec) == nodeB }
            ?: throw IllegalArgumentException("the positions are not adjacent")

        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunkA =
            dimension.getChunkAt(ChunkPos(nodeA)) ?: throw IllegalStateException("the chunk of nodeA is not loaded")
        transactionContext.markPartsRemoved(chunkA, nodeA, chunkA.removeEdge(nodeA, directionFromA, type))

        val chunkB =
            dimension.getChunkAt(ChunkPos(nodeB)) ?: throw IllegalStateException("the chunk of nodeB is not loaded")
        transactionContext.markPartsRemoved(chunkB, nodeB, chunkB.removeEdge(nodeB, directionFromA.opposite, type))
        transactionContext.markChunkModified(world.provider.dimension, chunkA)
        transactionContext.markChunkModified(world.provider.dimension, chunkB)
    }

    fun attachTransitSink(
        transactionContext: NetworkTransactionContext, world: WorldServer, pos: BlockPos,
        facing: EnumFacing, type: PipeType
    ) {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        transactionContext.markPartAdded(chunk, pos, chunk.insertEdge(pos, facing, type))
        transactionContext.markPartAdded(chunk, pos, chunk.attachTransitSink(pos, facing, type))
        transactionContext.markChunkModified(world.provider.dimension, chunk)
    }

    /**
     * Remove the edge and sink at a given location and facing. This will not check whether there is actually a sink
     * but just remove the edge. If there was no sink, the adjacent block in direction of [facing] will have a
     * dangling edge left.
     */
    fun removeTransitSink(
        transactionContext: NetworkTransactionContext, world: WorldServer, pos: BlockPos,
        facing: EnumFacing, type: PipeType
    ) {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        transactionContext.markPartsRemoved(chunk, pos, chunk.removeEdge(pos, facing, type))
        transactionContext.markChunkModified(world.provider.dimension, chunk)
    }

    /**
     * Remove all sinks that are attached to the given position of a given pipe type. This does also remove the edges
     * that are linked with the sinks. This does not remove the node.
     *
     * @param world the world of the conduit network
     * @param pos the position where to remove sinks
     * @param type the type of sinks to remove
     *
     */
    fun removeAllAttachedSinks(
        transactionContext: NetworkTransactionContext, world: WorldServer, pos: BlockPos,
        type: PipeType
    ) {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        transactionContext.markPartsRemoved(chunk, pos, chunk.removeAllSinks(pos, type))
        transactionContext.markChunkModified(world.provider.dimension, chunk)
    }

    /**
     * @param world the world object where to look for the pipe
     * @param pos the queried block position
     * @param type the pipe to look for
     *
     * @return true if the conduit network has a node of given type at given block position
     */
    fun hasConduitNode(world: WorldServer, pos: BlockPos, type: PipeType): Boolean {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        return chunk.hasNode(pos, type)
    }

    fun hasSink(world: WorldServer, pos: BlockPos, face: EnumFacing, type: PipeType): Boolean {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        return chunk.hasSink(pos, face, type)
    }

    /**
     * Get a list of [Part]s that represent all pipe nodes, edges and machine attachments that are present in the
     * given position.
     */
    fun getNodeParts(world: World, pos: BlockPos): Collection<Part> {
        val dimension =
            dimensions[world.provider.dimension] ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")
        val parts = mutableListOf<Part>()
        parts += chunk.getNodes(pos).map { NodePart(it) }
        parts += chunk.getEdges(pos).flatMap { (type, set) -> set.map { EdgePart(type, it) } }
        parts += chunk.getAttachments(pos).flatMap { (type, set) -> set.map { AttachmentPart(type, it) } }

        return parts
    }

    fun beginTransaction(): NetworkTransactionContext {
        return NetworkTransactionContext()
    }

    /**
     * Tick the conduit network. This will perform routing algorithms and actually transfer contents
     */
    fun tick(world: WorldServer) {
        if (this.dimensions.containsKey(world.provider.dimension)) {
            this.dimensions[world.provider.dimension]!!.tick(world)
        }
    }

    /**
     * Context of a network modification. Stores all modified chunks. After a full transaction, call [commit] to
     * recalculate network paths.
     */
    class NetworkTransactionContext internal constructor() {
        private var dimension: Int = Int.MIN_VALUE
        private val modifiedChunks: MutableList<ConduitNetworkChunk> = mutableListOf()
        private val removedParts: MutableMap<ConduitNetworkChunk, MutableList<Pair<BlockPos, Part>>> = mutableMapOf()
        private val addedParts: MutableMap<ConduitNetworkChunk, MutableList<Pair<BlockPos, Part>>> = mutableMapOf()

        internal fun markChunkModified(dimension: Int, chunk: ConduitNetworkChunk) {
            if (this.dimension == Int.MIN_VALUE) {
                this.dimension = dimension
            } else {
                if (this.dimension != dimension) {
                    error("cannot edit multiple dimensions in a single transaction")
                }
            }

            if (!modifiedChunks.contains(chunk)) {
                modifiedChunks += chunk
            }
        }

        internal fun markPartsRemoved(chunk: ConduitNetworkChunk, pos: BlockPos, parts: Collection<Part>) {
            removedParts.getOrPut(chunk) { mutableListOf() }.addAll(parts.map { Pair(pos, it) })
        }

        internal fun markPartAdded(chunk: ConduitNetworkChunk, pos: BlockPos, part: Part) {
            addedParts.getOrPut(chunk) { mutableListOf() }.add(Pair(pos, part))
        }

        fun commit(world: WorldServer) {
            modifiedChunks.forEach(ConduitNetworkChunk::recalculatePaths)

            modifiedChunks.forEach { chunk ->
                world.playerChunkMap.getEntry(chunk.chunkPos.x, chunk.chunkPos.z)!!.watchingPlayers.forEach { player ->
                    PacketHandler.sendToClient(
                        ServerConduitNetworkUpdatePacket(
                            this.dimension,
                            this.addedParts[chunk] ?: emptyList(),
                            this.removedParts[chunk] ?: emptyList()
                        ),
                        player
                    )
                }
            }
        }
    }

    /**
     * When a chunk is read from NBT, it has been loaded before and might have conduit data written to it. That must
     * be retrieved
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkDataLoad(event: ChunkDataEvent.Load) {
        if (event.world.isRemote) return

        val dimensionId = event.world.provider.dimension

        val dimension = dimensions.getOrPut(dimensionId, { ConduitNetworkDimension(dimensionId) })
        dimension.loadChunkData(event)
    }

    /**
     * When a chunk is being saved to NBT, but does not exist within the network, it is being generated. We can add
     * it to the list of loaded chunks as it is being loaded obviously.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkDataSave(event: ChunkDataEvent.Save) {
        if (event.world.isRemote) return

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
        if (event.world.isRemote) return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions.getOrPut(dimensionId, { ConduitNetworkDimension(dimensionId) })
        dimension.loadChunk(event.chunk)
    }

    /**
     * When a chunk gets unloaded, remove it from the dimension.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkUnload(event: ChunkEvent.Unload) {
        // only do that if we are either the server, or a client that is not connected to the internal server.
        // This ensures that the client does not execute this code twice. Make sure `Minecraft.getMinecraft()` is
        // never evaluated on the server
        if (!event.world.isRemote || !Minecraft.getMinecraft().isSingleplayer) {
            val dimensionId = event.world.provider.dimension
            val dimension = dimensions[dimensionId]!!

            dimension.unloadChunk(event.chunk)
        }
    }

    /**
     * When a player starts watching a chunk, synchronize the pipe structure to them, so it can be rendered correctly.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkBeginWatching(event: ChunkWatchEvent.Watch) {
        val chunk = event.chunkInstance!!
        val world = chunk.world

        if (!world.isRemote) {
            val dim = (world as WorldServer).provider.dimension
            val networkChunk = this.dimensions[dim]!!.getChunkAt(chunk.pos)!!
            val pipeData = NBTTagCompound()

            networkChunk.serializePipeStructure(pipeData)
            PacketHandler.sendToClient(ServerConduitNetworkSyncPacket(dim, pipeData, chunk.pos), event.player)
        }
    }

    /**
     * When a world is unloaded (most importantly when leaving a single player world, or a server), unload it from
     * the network. This both saves on memory, and prevents state corruption when dimensions between different world
     * files are mixed up.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        this.dimensions.remove(event.world.provider.dimension)
    }

    /**
     * Receive data about the pipe structure in a [ConduitNetworkChunk]. If
     * we are in singleplayer, the updates must be ignored, as the [ConduitNetwork] does not differentiate between
     * server state and client state, and as such already has all this data available.
     */
    @SideOnly(Side.CLIENT)
    fun receiveNetworkChunk(dimension: Int, chunkPos: ChunkPos, data: NBTTagCompound) {
        if (!Minecraft.getMinecraft().isSingleplayer) {
            val dim = this.dimensions.getOrPut(dimension) { ConduitNetworkDimension(dimension) }
            dim.loadEmptyChunk(chunkPos).deserializePipeStructure(data)
        }
    }

    /**
     * Receive updates to the pipe state in a chunk. The updates are sent by the server to all relevant clients. If
     * we are in singleplayer, the updates must be ignored, as the [ConduitNetwork] does not differentiate between
     * server state and client state, and as such already has all this data available.
     */
    @SideOnly(Side.CLIENT)
    fun receiveNetworkUpdates(dimension: Int, added: List<Pair<BlockPos, Part>>, removed: List<Pair<BlockPos, Part>>) {
        if (!Minecraft.getMinecraft().isSingleplayer) {
            val dim = this.dimensions.getOrPut(dimension) { ConduitNetworkDimension(dimension) }

            for ((pos, part) in added) {
                val chunkPos = ChunkPos(pos)
                dim.getChunkAt(chunkPos)?.receivePart(pos, part)
            }

            for ((pos, part) in removed) {
                val chunkPos = ChunkPos(pos)
                dim.getChunkAt(chunkPos)?.removePart(pos, part)
            }
        }
    }
}