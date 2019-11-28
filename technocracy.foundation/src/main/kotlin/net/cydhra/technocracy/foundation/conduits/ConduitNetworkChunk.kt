package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.conduits.transit.TransitChunkEdge
import net.cydhra.technocracy.foundation.conduits.transit.TransitEdge
import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import java.util.*

/**
 * A model for a chunk of the conduit network. Can be removed from the conduit network dimension without breaking
 * routing algorithms. Note, that this model does never save the chunk directly to prevent memory leaks, as the
 * [ConduitNetworkChunk] instance may remain in memory even after the chunk was unloaded.
 *
 * @param chunkPos the chunk position in the dimension
 */
internal class ConduitNetworkChunk(private val chunkPos: ChunkPos) : INBTSerializable<NBTTagCompound> {

    companion object {
        private const val NBT_KEY_TRANSIT_COUNTER_STATE = "transit_counter"
        private const val NBT_KEY_NODE_LIST = "nodes"
        private const val NBT_KEY_EDGE_LIST = "edges"
        private const val NBT_KEY_NODE_POS = "pos"
        private const val NBT_KEY_NODE_TYPE_LIST = "types"
        private const val NBT_KEY_EDGE_POS = "pos"
        private const val NBT_KEY_EDGE_TYPE_LIST = "types"
        private const val NBT_KEY_EDGE_TYPE_LIST_ENTRY = "type"
        private const val NBT_KEY_EDGE_TYPE_LIST_DIRECTION_LIST = "directions"
        private const val NBT_KEY_ATTACHMENTS_POS = "pos"
        private const val NBT_KEY_SINKS_LIST = "types"
        private const val NBT_KEY_ATTACHMENTS_LIST = "sinks"
        private const val NBT_KEY_TRANSIT_EDGE_POS = "pos"
        private const val NBT_KEY_TRANSIT_EDGES_TYPE_LIST = "transit_edges"
        private const val NBT_KEY_CHUNK_TRANSIT_EDGES_LIST = "transit_edges"
    }

    /**
     * A counter that gets increased, every time the conduit network within this chunk is modified. Checking this
     * counter against a previously saved value can verify that the chunk's network is unchanged.
     */
    internal var cacheValidationCounter: Int = 0
        private set

    /**
     * A counter to keep track of transit edge ids.
     */
    private var transitEdgeCounter: Int = 0

    /**
     * A mutable mapping of block positions to their respective node types
     */
    private val nodes: MutableMap<BlockPos, MutableSet<PipeType>> = mutableMapOf()

    val debug_nodes: Map<BlockPos, MutableSet<PipeType>> = nodes

    /**
     * A mutable mapping of block positions to a mapping of pipe types to a set of directions this type has edges in.
     * All edges are saved twice, so both connected block positions have an entry for the edge. This increases memory
     * use, but is considered worth for the reduced complexity of obtaining edge lists
     */
    private val edges: MutableMap<BlockPos, MutableMap<PipeType, MutableSet<EnumFacing>>> = mutableMapOf()

    val debug_edges: Map<BlockPos, Map<PipeType, Set<EnumFacing>>> = edges

    /**
     * A mutable mapping of block positions to a mapping of pipe types to a set of directions where edges interface
     * with sinks. Since sinks are kept within the same block as the pipe, a sink that is attached to the pipe but
     * resides in a neighbor chunk is still visible. Whether it is loaded must be checked separately.
     */
    private val attachedSinks: MutableMap<BlockPos, MutableSet<TransitSink>> = mutableMapOf()

    val debug_sinks: Map<BlockPos, Set<TransitSink>> = attachedSinks

    /**
     * Transit edges that connect a conduit node to a different chunk
     */
    private val chunkTransitEdges: MutableMap<BlockPos, MutableSet<TransitChunkEdge>> = mutableMapOf()

    val debug_transits: Map<BlockPos, Set<TransitChunkEdge>> = chunkTransitEdges

    /**
     * Add a node to the conduit network. This method does only add this one node to the network: no additional nodes
     * are discovered in the neighborhood of the block. If the node already exists, an [IllegalStateException] is
     * thrown. No edges are inserted into the network. It is asserted that [pos] is within the chunk modeled by this
     * instance.
     *
     * @param pos position in world where the node is added
     * @param type pipe type of the new node. Each node does only have one pipe type. Multiple pipe types within the
     * same block must be added individually.
     *
     * @throws IllegalStateException if the node already exists
     */
    internal fun insertNode(pos: BlockPos, type: PipeType) {
        check(this.nodes[pos]?.contains(type) != true) { "the inserted node already exists within the network" }

        if (!this.nodes.contains(pos)) {
            this.nodes[pos] = mutableSetOf()
        }

        this.nodes[pos]!!.add(type)
        this.markDirty()
    }

    /**
     * Remove a node from the conduit network. No edges from and to the node are removed. If the node does
     * not exist, an [IllegalStateException] is thrown. It is asserted that [pos] is within the chunk modeled by this
     * instance.
     *
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     */
    internal fun removeNode(pos: BlockPos, type: PipeType) {
        check(this.nodes[pos]?.contains(type) != false) { "the removed node does not exist within the network" }

        this.nodes[pos]!!.remove(type)
        this.markDirty()
    }

    /**
     * Insert an edge into the conduit network. Only one end of the edge is inserted, facing in the given direction.
     * The caller must ensure that the other end of the pipe is also inserted, possibly in another chunk.
     * If the edge already exists, an [IllegalStateException] is thrown. The node position must contain a
     * node of given [type]. It is asserted that the given position is within this chunk.
     *
     * @param pos the origin of the edge
     * @param facing the direction of the edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalStateException] if the positions given are not adjacent
     * @throws [IllegalStateException] if one of the node positions does not contain a [type] node
     * @throws [IllegalStateException] if the edge already exists
     */
    internal fun insertEdge(pos: BlockPos, facing: EnumFacing, type: PipeType) {
        check(this.nodes[pos]?.contains(type) != false) { "position does not contain a node of type $type" }
        check(this.edges[pos]?.get(type)?.contains(facing) != true) { "the edge already existed" }

        if (!this.edges.containsKey(pos)) {
            this.edges[pos] = mutableMapOf()
        }

        if (!this.edges[pos]!!.containsKey(type)) {
            this.edges[pos]!![type] = mutableSetOf()
        }

        this.edges[pos]!![type]!! += facing

        val pointsTowardsPos = pos.offset(facing)
        if (this.chunkPos.xStart > pointsTowardsPos.x || this.chunkPos.xEnd < pointsTowardsPos.x || this.chunkPos.zStart > pointsTowardsPos.z || this.chunkPos.zEnd < pointsTowardsPos.z) {
            if (!chunkTransitEdges.containsKey(pos)) {
                chunkTransitEdges[pos] = mutableSetOf()
            }
            chunkTransitEdges[pos]!! += TransitChunkEdge(transitEdgeCounter++, type, facing, pos)
        }

        this.markDirty()
    }

    /**
     * Remove an edge from the conduit network. The edge must exist, otherwise an [IllegalStateException] is thrown.
     * No further nodes or edges are removed. It is asserted that the position is within the
     * chunk modeled by this instance. The edge pointing towards the removed edge (originating in the adjacent node) is
     * not removed. If this edge has a sink attached, it is removed as well.
     *
     * @param pos where the edge is located
     * @param facing the direction of the edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalStateException] if the positions given are not adjacent
     * @throws [IllegalStateException] if the edge does not exist
     */
    internal fun removeEdge(pos: BlockPos, facing: EnumFacing, type: PipeType) {
        check(this.edges[pos]?.get(type)?.contains(facing) != false) { "the edge does not exist" }

        this.edges[pos]!![type]!!.remove(facing)

        // remove attached sinks if any
        this.removeTransitSink(pos, facing, type)

        // remove transit edges if any
        if (!chunkTransitEdges[pos].isNullOrEmpty()) {
            chunkTransitEdges[pos]!!.removeIf { it.type == type && it.facing == facing }
        }

        this.markDirty()
    }

    /**
     * Attaches any form of source or sink to the conduit net. If the sink is removed, it won't be removed from the
     * conduit network automatically. The sink must be interfaceable using the given [type]
     *
     * @param pos the position of the edge that is attached to the sink.
     * @param facing the face that the sink block shares with the pipe block (viewed from the pipe block)
     * @param type the type of pipe that is interfacing with the sink
     *
     * @throws IllegalStateException if the sink is already attached
     */
    internal fun attachTransitSink(pos: BlockPos, facing: EnumFacing, type: PipeType) {
        check(!this.hasSink(pos, facing, type)) { "sink already attached" }

        if (this.attachedSinks[pos] == null) {
            this.attachedSinks[pos] = mutableSetOf()
        }

        this.attachedSinks[pos]!!.add(TransitSink(transitEdgeCounter++, type, facing, pos))
        this.markDirty()
    }

    /**
     * Removes a sink from the conduit net. The sink must have been previously attached to the very same [pos] and
     * [facing] using [attachTransitSink]. If such a sink does not exist, nothing happens.
     *
     * @param pos the position of the edge where the sink is attached to
     * @param facing the face that the sink block shares with the pipe block (viewed from the pipe block)
     * @param type the type of pipe that is interfacing with the sink
     */
    internal fun removeTransitSink(pos: BlockPos, facing: EnumFacing, type: PipeType) {
        this.attachedSinks[pos]?.removeIf { it.type == type && it.facing == facing }
        this.markDirty()
    }

    /**
     * Remove all sinks that link to a node at given position. This is called if the node is removed, so remaining
     * sinks can be removed as well. This method removes the sinks if present, and does nothing otherwise.
     */
    fun removeAllSinks(pos: BlockPos, type: PipeType) {
        // remove all edges that are linked to this type and thus their attachments
        this.attachedSinks[pos]?.filter { it.type == type }?.forEach { sink ->
            this.removeEdge(pos, sink.facing, type)
        }

        // if there is a sink entry present, remove the type entry (it is empty now anyway)
        this.attachedSinks[pos]?.removeIf { it.type == type }

        // if there is a sink entry present and it is empty now, remove it
        if (this.attachedSinks[pos]?.isEmpty() == true) {
            this.attachedSinks.remove(pos)
        }
    }

    /**
     * Returns true, if the chunk has a specified sink type facing a specified direction at a given position.
     *
     * @param pos block position
     * @param face which direction the sink attachment is facing at from [pos]
     * @param type sink pipe type
     */
    fun hasSink(pos: BlockPos, face: EnumFacing, type: PipeType): Boolean {
        return this.attachedSinks[pos]?.any { it.facing == face && it.type == type } ?: false
    }

    /**
     * Find transit edge with given id within this chunk.
     *
     * @return transit edge instance or null, if this id is not valid within this chunk
     */
    fun getTransitEdge(id: Int): Pair<BlockPos, TransitEdge>? {
        return this.chunkTransitEdges.entries
                .union(this.attachedSinks.entries)
                .mapNotNull { (pos, set) ->
                    val target = set.find { it.id == id }
                    if (target != null) {
                        Pair(pos, target)
                    } else {
                        null
                    }
                }
                .firstOrNull()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.transitEdgeCounter = nbt.getInteger(NBT_KEY_TRANSIT_COUNTER_STATE)

        val nodeList = nbt.getTagList(NBT_KEY_NODE_LIST, Constants.NBT.TAG_COMPOUND)
        val edgeList = nbt.getTagList(NBT_KEY_EDGE_LIST, Constants.NBT.TAG_COMPOUND)
        val attachmentList = nbt.getTagList(NBT_KEY_ATTACHMENTS_LIST, Constants.NBT.TAG_COMPOUND)
        val transitEdgesList = nbt.getTagList(NBT_KEY_CHUNK_TRANSIT_EDGES_LIST, Constants.NBT.TAG_COMPOUND)

        nodeList.forEach { nodeTag ->
            val blockPos = NBTUtil.getPosFromTag((nodeTag as NBTTagCompound).getCompoundTag(NBT_KEY_NODE_POS))
            val types = nodeTag.getIntArray(NBT_KEY_NODE_TYPE_LIST).map { PipeType.values()[it] }.toMutableSet()
            this.nodes[blockPos] = types
        }

        edgeList.forEach { edgeTag ->
            val blockPos = NBTUtil.getPosFromTag((edgeTag as NBTTagCompound).getCompoundTag(NBT_KEY_EDGE_POS))
            val typeList = edgeTag.getTagList(NBT_KEY_EDGE_TYPE_LIST, Constants.NBT.TAG_COMPOUND)

            this.edges[blockPos] = mutableMapOf()

            typeList.forEach { typeTag ->
                val pipeType = PipeType.values()[(typeTag as NBTTagCompound).getInteger(NBT_KEY_EDGE_TYPE_LIST_ENTRY)]
                val facings = typeTag.getIntArray(NBT_KEY_EDGE_TYPE_LIST_DIRECTION_LIST).map { EnumFacing.values()[it] }
                        .toMutableSet()

                this.edges[blockPos]!![pipeType] = facings
            }
        }

        attachmentList.forEach { attachmentTag ->
            val blockPos =
                    NBTUtil.getPosFromTag((attachmentTag as NBTTagCompound).getCompoundTag(NBT_KEY_ATTACHMENTS_POS))
            val sinkList = attachmentTag.getTagList(NBT_KEY_SINKS_LIST, Constants.NBT.TAG_COMPOUND)

            this.attachedSinks[blockPos] = mutableSetOf()

            sinkList.forEach { sinkTag ->
                this.attachedSinks[blockPos]!!.add(TransitSink(blockPos).apply { deserializeNBT(sinkTag as NBTTagCompound) })
            }
        }

        transitEdgesList.forEach { transitEdgeTag ->
            val blockPos =
                    NBTUtil.getPosFromTag((transitEdgeTag as NBTTagCompound).getCompoundTag(NBT_KEY_TRANSIT_EDGE_POS))
            val typeList = transitEdgeTag.getTagList(NBT_KEY_TRANSIT_EDGES_TYPE_LIST, Constants.NBT.TAG_COMPOUND)

            this.chunkTransitEdges[blockPos] = mutableSetOf()

            typeList.forEach { transitEdgeEntry ->
                this.chunkTransitEdges[blockPos]!!.add(TransitChunkEdge(blockPos)
                        .apply { deserializeNBT(transitEdgeEntry as NBTTagCompound) })
            }
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()

        compound.setInteger(NBT_KEY_TRANSIT_COUNTER_STATE, 1)

        val nodeList = NBTTagList()
        for (blockPos in this.nodes.keys) {
            val nodeTag = NBTTagCompound()
            nodeTag.setTag(NBT_KEY_NODE_POS, NBTUtil.createPosTag(blockPos))
            nodeTag.setTag(NBT_KEY_NODE_TYPE_LIST, NBTTagIntArray(this.nodes[blockPos]!!.map(PipeType::ordinal)))
            nodeList.appendTag(nodeTag)
        }

        val edgeList = NBTTagList()
        for (edgePos in this.edges.keys) {
            val edgeTag = NBTTagCompound()
            edgeTag.setTag(NBT_KEY_EDGE_POS, NBTUtil.createPosTag(edgePos))

            val typeList = NBTTagList()
            for (pipeType in this.edges[edgePos]!!.keys) {
                val typeTag = NBTTagCompound()
                typeTag.setInteger(NBT_KEY_EDGE_TYPE_LIST_ENTRY, pipeType.ordinal)
                typeTag.setTag(NBT_KEY_EDGE_TYPE_LIST_DIRECTION_LIST,
                        NBTTagIntArray(this.edges[edgePos]!![pipeType]!!.map(EnumFacing::ordinal)))
                typeList.appendTag(typeTag)
            }

            edgeTag.setTag(NBT_KEY_EDGE_TYPE_LIST, typeList)
            edgeList.appendTag(edgeTag)
        }

        val attachmentList = NBTTagList()
        for (sinkPos in this.attachedSinks.keys) {
            val attachmentTag = NBTTagCompound()
            attachmentTag.setTag(NBT_KEY_ATTACHMENTS_POS, NBTUtil.createPosTag(sinkPos))

            val sinksList = NBTTagList()
            for (sink in this.attachedSinks[sinkPos]!!) {
                sinksList.appendTag(sink.serializeNBT())
            }

            attachmentTag.setTag(NBT_KEY_SINKS_LIST, sinksList)
            attachmentList.appendTag(attachmentTag)
        }

        val transitEdgeList = NBTTagList()
        for (transitEdgePos in this.chunkTransitEdges.keys) {
            val transitEdgeTag = NBTTagCompound()
            transitEdgeTag.setTag(NBT_KEY_TRANSIT_EDGE_POS, NBTUtil.createPosTag(transitEdgePos))

            val typeList = NBTTagList()
            for (edge in this.chunkTransitEdges[transitEdgePos]!!) {
                typeList.appendTag(edge.serializeNBT())
            }

            transitEdgeTag.setTag(NBT_KEY_TRANSIT_EDGES_TYPE_LIST, typeList)
            transitEdgeList.appendTag(transitEdgeTag)
        }

        compound.setTag(NBT_KEY_NODE_LIST, nodeList)
        compound.setTag(NBT_KEY_EDGE_LIST, edgeList)
        compound.setTag(NBT_KEY_ATTACHMENTS_LIST, attachmentList)
        compound.setTag(NBT_KEY_CHUNK_TRANSIT_EDGES_LIST, transitEdgeList)

        return compound
    }

    /**
     * Mark this chunk as dirty
     */
    private fun markDirty() {
        cacheValidationCounter++
    }

    /**
     * Recalculate the internal transit network
     */
    internal fun recalculatePaths() {
        val transitEndpoints =
                mutableMapOf(*PipeType.values()
                        .map { it to ArrayDeque<Pair<BlockPos, TransitEdge>>() }
                        .toTypedArray())

        this.attachedSinks.entries
                .union(this.chunkTransitEdges.entries)
                .map { (pos, set) -> set.map { sink -> pos to sink } }
                .flatten()
                .groupBy { (_, edge) -> edge.type }
                .forEach { (pipeType, list) ->
                    transitEndpoints[pipeType]!!.addAll(list)
                }

        for ((pipeType, transitQueue) in transitEndpoints) {
            val transitEdgeQueue = transitQueue.clone()

            while (transitEdgeQueue.isNotEmpty()) {
                val currentConnectedTransitComponent = mutableMapOf<TransitEdge, Int>()
                val currentKnownComponent = mutableMapOf<BlockPos, Int>()
                val (currentTransitPosition, currentTransitEdge) = transitEdgeQueue.pop()

                // the first position has distance one from the actual transit sink
                currentKnownComponent[currentTransitPosition] = 1
                currentConnectedTransitComponent[currentTransitEdge] = 0

                // inner loop state
                var currentPosition: BlockPos
                val positionQueue = ArrayDeque<BlockPos>()
                positionQueue.add(currentTransitPosition)

                while (positionQueue.isNotEmpty()) {
                    currentPosition = positionQueue.pop()

                    this.edges[currentPosition]!![pipeType]!!.forEach { facing ->
                        val neighbor = currentPosition.offset(facing)

                        // test if the edge actually leads to a node (instead of being a transit edge)
                        if (this.nodes[neighbor]?.contains(pipeType) == true) {
                            // if neighbor is already known
                            if (currentKnownComponent[neighbor] != null) {
                                if (currentKnownComponent[neighbor]!! > currentKnownComponent[currentPosition]!! + 1) {
                                    currentKnownComponent[neighbor] = currentKnownComponent[currentPosition]!! + 1
                                }
                            } else {
                                currentKnownComponent[neighbor] = currentKnownComponent[currentPosition]!! + 1
                                positionQueue.add(neighbor)
                            }
                        } else {
                            val transitEdge = transitEndpoints[pipeType]
                                    ?.find { (pos, edge) -> pos == currentPosition && edge.facing == facing }
                                    ?.second

                            if (transitEdge != null) {
                                if (currentConnectedTransitComponent.containsKey(transitEdge)) {
                                    if (currentConnectedTransitComponent[transitEdge]!! > currentKnownComponent[currentPosition]!! + 1) {
                                        currentConnectedTransitComponent[transitEdge] =
                                                currentKnownComponent[currentPosition]!! + 1
                                    }
                                } else {
                                    currentConnectedTransitComponent[transitEdge] =
                                            currentKnownComponent[currentPosition]!! + 1
                                }
                            }
                        }
                    }
                }

                currentTransitEdge.paths.clear()
                currentTransitEdge.paths.putAll(currentConnectedTransitComponent
                        .filter { (_, cost) -> cost > 0 }.map { (edge, cost) -> Pair(edge.id, cost) }.toMap())
            }
        }
    }

    /**
     * Check whether a given block position has a type of pipe node in it. This does assume, that [pos] is actually
     * within this chunk.
     *
     * @param pos block position within this chunk
     * @param type type of pipe to check
     *
     * @return true if there is a node of given type at given position, false if not.
     */
    fun hasNode(pos: BlockPos, type: PipeType): Boolean {
        return nodes[pos]?.contains(type) ?: false
    }

    /**
     * Get a list of all transit edges within this chunk. The list can not be mutated. All edges contain their
     * respective path cost entries to other edges within the chunk.
     */
    fun getTransitEdges(): List<TransitEdge> {
        return this.chunkTransitEdges.values
                .union(this.attachedSinks.values)
                .flatten()
    }
}