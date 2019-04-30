package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.Multigraph
import org.jgrapht.traverse.DepthFirstIterator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.function.Consumer


object Network {
    var networks = mutableMapOf<String, MutableMap<UUID, Multigraph<BlockPos, WrappedPipeType>>>()
    val saveName = "${TCFoundation.MODID}:pipe_network"

    var savedData = WorldPipeData(this)

    var dirty = false

    fun loadEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World,
                 type: PipeType) {
        if (world.isRemote)
            return
        val network = networks[world.worldInfo.worldName]!![networkId]!!
        network.addEdge(connectToBlock, newBlockPos, WrappedPipeType(type))
    }

    fun addEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World,
                type: PipeType) {
        if (world.isRemote)
            return
        addNode(newBlockPos, networkId, world)
        addNode(connectToBlock, networkId, world)

        val network = networks[world.worldInfo.worldName]!![networkId]!!

        if (network.getAllEdges(connectToBlock, newBlockPos).none { it.pipeType == type }) {
            network.addEdge(connectToBlock, newBlockPos, WrappedPipeType(type))
            dirty = true
        }
    }

    fun removeEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World, type:
    PipeType) {
        if (world.isRemote)
            return
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        val edge = network.getAllEdges(newBlockPos, connectToBlock).find { it.pipeType == type }
        network.removeEdge(edge)

        dirty = true
    }

    fun removeNode(node: BlockPos, networkId: UUID, world: World) {
        if (world.isRemote)
            return
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        val edges = network.edgesOf(node)
        if (edges.size > 1) {
            edges.forEach(Consumer { edge ->
                val edgetmp = network.getEdgeSource(edge)
                val secondNode = if (edgetmp == node) network.getEdgeTarget(edge) else edgetmp

                splitNetwork(secondNode, networkId, world)
            })
        }

        network.removeVertex(node)

        dirty = true
    }

    fun removeNodeInEveryNetwork(node: BlockPos, world: World) {
        if (world.isRemote)
            return
        networks[world.worldInfo.worldName]!!.forEach { id, network ->
            if (network.containsVertex(node)) {
                val edges = network.edgesOf(node)
                if (edges.size != 0) {
                    edges.forEach { edge ->
                        val edgetmp = network.getEdgeSource(edge)
                        val secondNode = if (edgetmp == node) network.getEdgeTarget(edge) else edgetmp

                        splitNetwork(secondNode, id, world)
                    }
                }

                network.removeVertex(node)
            }
        }
        dirty = true
    }

    fun addNode(node: BlockPos, networkId: UUID, world: World) {
        if (world.isRemote)
            return
        val map = networks.getOrPut(world.worldInfo.worldName) {
            mutableMapOf(Pair(networkId, Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

        val network = map.getOrPut(networkId) {
            Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        }

        if (!network.vertexSet().contains(node)) {
            network.addVertex(node)
            dirty = true
        }
    }

    fun loadNode(node: BlockPos, networkId: UUID, world: World) {
        if (world.isRemote)
            return
        val map = networks.getOrPut(world.worldInfo.worldName) {
            mutableMapOf(Pair(networkId, Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class
                    .java).build()))
        }

        val network = map.getOrPut(networkId) {
            Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        }

        if (!network.vertexSet().contains(node)) {
            network.addVertex(node)
        }
    }

    fun areNodesConnected(nodeA: BlockPos, nodeB: BlockPos, networkId: UUID, world: World, type:
    PipeType): Boolean {
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        val edges = network.edgeSet().filter { it.pipeType == type }

        val walker = GraphWalk(network, nodeA, nodeB, edges, 0.0)

        try {
            walker.verify()
        } catch (e: RuntimeException) {
            return false
        }

        return true
    }

    fun combineNet1work(nodeA: BlockPos, nodeB: BlockPos, networkId_old: UUID, networkId_new: UUID, world: World, type:
    PipeType) {
        if (world.isRemote)
            return
        val network_old = networks[world.worldInfo.worldName]!![networkId_old]!!
        var network_new = networks[world.worldInfo.worldName]!![networkId_new]!!

        network_old.vertexSet().forEach {
            network_new.addVertex(it)
            forceNetworkId(it, networkId_new, world)
        }
        network_old.edgeSet().forEach { network_new.addEdge(it.getSourceNode(), it.getTargetNode(), it) }
        network_new.addEdge(nodeA, nodeB, WrappedPipeType(type))

        networks[world.worldInfo.worldName]!!.remove(networkId_old)

        /*val builder = Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java)
        builder.addGraph(network_old)
        builder.addGraph(network_new)
        network_new = builder.build()
        network_new.addEdge(nodeA, nodeB, WrappedPipeType(type))

        networks[world.worldInfo.worldName]!!.remove(networkId_old)

        val iterator = DepthFirstIterator(network_old, if (network_old.containsVertex(nodeB)) nodeB else nodeA)

        iterator.forEach { firstNode ->
            forceNetworkId(firstNode, networkId_new, world)
        }*/
        dirty = true
    }

    fun splitNetwork(nodeA: BlockPos, networkId: UUID, world: World) {
        if (world.isRemote)
            return
        val network = networks[world.worldInfo.worldName]!![networkId]!!
        val newNetwork = Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        val newUUID = UUID.randomUUID()
        val iterator = DepthFirstIterator(network, nodeA)
        val removeList = mutableSetOf<BlockPos>()

        iterator.forEach { firstNode ->
            newNetwork.addVertex(firstNode)
            removeList.add(firstNode)

            val edges = network.edgesOf(firstNode)
            edges.forEach { edge ->
                val src = edge.getSourceNode()
                val taregt = edge.getTargetNode()
                newNetwork.addVertex(src)
                newNetwork.addVertex(taregt)
                newNetwork.addEdge(src, taregt, edge)
            }
        }
        network.removeAllVertices(removeList)

        networks[world.worldInfo.worldName] = mutableMapOf(Pair(newUUID, newNetwork))

        forceNetworkId(removeList, newUUID, world)
        dirty = true
    }

    fun forceNetworkId(blocks: MutableSet<BlockPos>, uuid: UUID, world: World) {
        blocks.forEach {
            val tile = world.getTileEntity(it)
            if (tile != null && tile is TileEntityPipe) {
                tile.setNetworkId(uuid)
            }
        }
    }

    fun forceNetworkId(block: BlockPos, uuid: UUID, world: World) {
        val tile = world.getTileEntity(block)
        if (tile != null && tile is TileEntityPipe) {
            tile.setNetworkId(uuid)
        }
    }

    @SubscribeEvent
    fun initWorld(e: WorldEvent.Load) {
        if (!e.world.isRemote) {
            val file = File(DimensionManager.getCurrentSaveRootDirectory()!!.absolutePath + "./pipes.data")
            if (file.exists())
                savedData.readNBT(e.world, CompressedStreamTools.readCompressed(FileInputStream(file)))
        }
    }

    @SubscribeEvent
    fun worldSave(e: WorldEvent.Save) {
        if (!e.world.isRemote && dirty) {
            dirty = false
            val file = File(DimensionManager.getCurrentSaveRootDirectory()!!.absolutePath + "./pipes.data")
            CompressedStreamTools.writeCompressed(savedData.writeNBT(), FileOutputStream(file))
        }
    }

    data class WrappedPipeType(val pipeType: PipeType) : DefaultEdge() {
        val uuid: UUID? = UUID.randomUUID()
        fun getSourceNode(): BlockPos {
            return super.getSource() as BlockPos
        }

        fun getTargetNode(): BlockPos {
            return super.getTarget() as BlockPos
        }

        override fun equals(other: Any?): Boolean {
            return other is WrappedPipeType && other.uuid?.equals(uuid)!!
        }

        override fun hashCode(): Int {
            return uuid.hashCode()
        }
    }

    enum class PipeType {
        ENERGY, FLUID, ITEM
    }
}