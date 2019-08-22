package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable

/**
 * A model for a chunk of the conduit network. Can be removed from the conduit network dimension without breaking
 * routing algorithms. Note, that this model does never save the chunk directly to prevent memory leaks, as the
 * [ConduitNetworkChunk] instance may remain in memory even after the chunk was unloaded.
 *
 * @param chunkPos the chunk position in the dimension
 */
internal class ConduitNetworkChunk(private val chunkPos: ChunkPos) : INBTSerializable<NBTTagCompound> {

    companion object {
        private const val NBT_KEY_NODE_LIST = "nodes"
        private const val NBT_KEY_EDGE_LIST = "edges"
        private const val NBT_KEY_NODE_POS = "pos"
        private const val NBT_KEY_NODE_TYPE_LIST = "types"
        private const val NBT_KEY_EDGE_POS = "pos"
        private const val NBT_KEY_EDGE_TYPE_LIST = "types"
        private const val NBT_KEY_EDGE_TYPE_LIST_ENTRY = "type"
        private const val NBT_KEY_EDGE_TYPE_LIST_DIRECTION_LIST = "directions"
    }

    /**
     * A counter that gets increased, every time the conduit network within this chunk is modified. Checking this
     * counter against a previously saved value can verify that the chunk's network is unchanged.
     */
    internal var cacheValidationCounter: Int = 0
        private set

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
    private val attachedSinks: MutableMap<BlockPos, MutableMap<PipeType, MutableSet<EnumFacing>>> = mutableMapOf()

    val debug_sinks: Map<BlockPos, Map<PipeType, Set<EnumFacing>>> = attachedSinks

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
        if (this.nodes[pos]?.contains(type) == true)
            throw IllegalStateException("the inserted node already exists within the network")

        if (!this.nodes.contains(pos)) {
            this.nodes[pos] = mutableSetOf()
        }

        this.nodes[pos]!!.add(type)
        this.recalculatePaths()
        this.markDirty()
    }

    /**
     * Remove a node from the conduit network. All edges from and to the node are removed as well. If the node does
     * not exist, an [IllegalStateException] is thrown. It is asserted that [pos] is within the chunk modeled by this
     * instance.
     *
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     */
    internal fun removeNode(pos: BlockPos, type: PipeType) {
        if (this.nodes[pos]?.contains(type) == false)
            throw IllegalStateException("the removed node does not exist within the network")

        this.nodes[pos]!!.remove(type)
        this.recalculatePaths()
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
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalArgumentException] if one of the node positions does not contain a [type] node
     * @throws [IllegalStateException] if the edge already exists
     */
    internal fun insertEdge(pos: BlockPos, facing: EnumFacing, type: PipeType) {

        if (this.nodes[pos]?.contains(type) == false)
            throw IllegalArgumentException("position does not contain a node of type $type")

        if (this.edges[pos]?.get(type)?.contains(facing) == true) {
            throw IllegalStateException("the edge already existed")
        }

        if (!this.edges.containsKey(pos)) {
            this.edges[pos] = mutableMapOf()
        }

        if (!this.edges[pos]!!.containsKey(type)) {
            this.edges[pos]!![type] = mutableSetOf()
        }

        this.edges[pos]!![type]!! += facing

        this.recalculatePaths()
        this.markDirty()
    }

    /**
     * Remove an edge from the conduit network. The edge must exist, otherwise an [IllegalStateException] is thrown.
     * No further nodes or edges are removed. It is asserted that the position is within the
     * chunk modeled by this instance. The edge pointing towards the removed edge (originating in the adjacent node) is
     * not removed.
     *
     * @param pos where the edge is located
     * @param facing the direction of the edge
     * @param type pipe type of the edge
     *
     * @throws [IllegalArgumentException] if the positions given are not adjacent
     * @throws [IllegalStateException] if the edge does not exist
     */
    internal fun removeEdge(pos: BlockPos, facing: EnumFacing, type: PipeType) {
        if (this.edges[pos]?.get(type)?.contains(facing) == false) {
            throw IllegalStateException("the edge does not exist")
        }

        this.edges[pos]!![type]!!.remove(facing)
        this.recalculatePaths()
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
        if (this.attachedSinks[pos]?.get(type)?.contains(facing) == true) {
            throw IllegalStateException("sink already attached")
        }

        if (this.attachedSinks[pos] == null) {
            this.attachedSinks[pos] = mutableMapOf()
        }

        if (this.attachedSinks[pos]!![type] == null) {
            this.attachedSinks[pos]!![type] = mutableSetOf()
        }

        this.attachedSinks[pos]!![type]!!.add(facing)
        this.recalculatePaths()
        this.markDirty()
    }

    /**
     * Removes a sink from the conduit net. The sink must have been previously attached to the very same [pos] and
     * [facing] using [attachTransitSink].
     *
     * @param pos the position of the edge where the sink is attached to
     * @param facing the face that the sink block shares with the pipe block (viewed from the pipe block)
     * @param type the type of pipe that is interfacing with the sink
     */
    internal fun removeTransitSink(pos: BlockPos, facing: EnumFacing, type: PipeType) {
        if (this.attachedSinks[pos]?.get(type)?.contains(facing) == false) {
            throw IllegalStateException("there is no such sink to remove")
        }

        this.attachedSinks[pos]!![type]!!.remove(facing)
        this.recalculatePaths()
        this.markDirty()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val nodeList = nbt.getTagList(NBT_KEY_NODE_LIST, Constants.NBT.TAG_COMPOUND)
        val edgeList = nbt.getTagList(NBT_KEY_EDGE_LIST, Constants.NBT.TAG_COMPOUND)

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
                val facings = typeTag.getIntArray(NBT_KEY_EDGE_TYPE_LIST_DIRECTION_LIST)
                        .map { EnumFacing.values()[it] }
                        .toMutableSet()

                this.edges[blockPos]!![pipeType] = facings
            }
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()

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

        compound.setTag(NBT_KEY_NODE_LIST, nodeList)
        compound.setTag(NBT_KEY_EDGE_LIST, edgeList)

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
    private fun recalculatePaths() {
        // TODO recalculate transit model
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
}