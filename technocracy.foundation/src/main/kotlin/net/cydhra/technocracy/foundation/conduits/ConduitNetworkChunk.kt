package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
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

    /**
     * A mutable mapping of block positions to a mapping of pipe types to a set of directions this type has edges in.
     * All edges are saved twice, so both connected block positions have an entry for the edge. This increases memory
     * use, but is considered worth for the reduced complexity of obtaining edge lists
     */
    private val edges: MutableMap<BlockPos, MutableMap<PipeType, MutableSet<EnumFacing>>> = mutableMapOf()

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
     * instance and [world] is the owner of that chunk.
     *
     * @param world world server that removes the node
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     */
    internal fun removeNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        if (this.nodes[pos]?.contains(type) == false)
            throw IllegalStateException("the removed node does not exist within the network")

        this.nodes[pos]!!.remove(type)
        this.recalculatePaths()
        this.markDirty()
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
        val directionFromA = EnumFacing.values().firstOrNull { nodeA.add(it.directionVec) == nodeB }
                ?: throw IllegalArgumentException("the positions are not adjacent")

        if (this.nodes[nodeA]?.contains(type) == false)
            throw IllegalArgumentException("position A does not contain a node of type $type")

        if (this.nodes[nodeB]?.contains(type) == false)
            throw IllegalArgumentException("position B does not contain a node of type $type")

        if (this.edges[nodeA]?.get(type)?.contains(directionFromA) == true) {
            throw IllegalStateException("the edge already existed")
        }

        assert(this.edges[nodeB]?.get(type)?.contains(directionFromA.opposite) == false)

        if (!this.edges.containsKey(nodeA)) {
            this.edges[nodeA] = mutableMapOf()
        }

        if (!this.edges[nodeA]!!.containsKey(type)) {
            this.edges[nodeA]!![type] = mutableSetOf()
        }

        this.edges[nodeA]!![type]!! += directionFromA

        if (!this.edges.containsKey(nodeB)) {
            this.edges[nodeB] = mutableMapOf()
        }

        if (!this.edges[nodeB]!!.containsKey(type)) {
            this.edges[nodeB]!![type] = mutableSetOf()
        }

        this.edges[nodeB]!![type]!! += directionFromA.opposite
        this.recalculatePaths()
        this.markDirty()
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
        val directionFromA = EnumFacing.values().firstOrNull { nodeA.add(it.directionVec) == nodeB }
                ?: throw IllegalArgumentException("the positions are not adjacent")

        if (this.edges[nodeA]?.get(type)?.contains(directionFromA) == false) {
            throw IllegalStateException("the edge does not exist")
        }

        assert(this.edges[nodeB]?.get(type)?.contains(directionFromA.opposite) == true)

        this.edges[nodeA]!![type]!!.remove(directionFromA)
        this.edges[nodeB]!![type]!!.remove(directionFromA.opposite)

        this.recalculatePaths()
        this.markDirty()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {

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