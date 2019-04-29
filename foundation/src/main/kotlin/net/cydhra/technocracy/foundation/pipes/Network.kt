package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.Multigraph
import org.jgrapht.traverse.DepthFirstIterator
import java.util.*
import java.util.function.Consumer


object Network {
    var networks = mutableMapOf<String, MutableMap<UUID, Multigraph<BlockPos, WrappedPipeType>>>()
    val saveName = "${TCFoundation.MODID}:pipe_network"

    fun addEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World, type:
    PipeType) {
        val network = networks[world.worldInfo.worldName]!![networkId]!!
        network.addEdge(connectToBlock, newBlockPos, WrappedPipeType(type))

        getWorldSave().markDirty()
    }

    fun removeEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World, type:
    PipeType) {
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        val edge = network.getAllEdges(newBlockPos, connectToBlock).find { it.pipeType == type }
        network.removeEdge(edge)

        getWorldSave().markDirty()
    }

    fun removeNode(node: BlockPos, networkId: UUID, world: World) {
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        val edges = network.edgesOf(node)
        if(edges.size > 1) {
            edges.forEach(Consumer { edge ->
                val edgetmp = network.getEdgeSource(edge)
                val secondNode = if (edgetmp == node) network.getEdgeTarget(edge) else edgetmp

                splitNetwork(secondNode,networkId, world)
            })
        }

        network.removeVertex(node)

        getWorldSave().markDirty()
    }

    fun addNode(node: BlockPos, networkId: UUID, world: World) {

        networks.getOrPut(world.worldInfo.worldName) {
            mutableMapOf(Pair(networkId, Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

        println("$node $networkId ${world.worldInfo.worldName} ${networks[world.worldInfo.worldName]}")

        val network = networks[world.worldInfo.worldName]!![networkId]!!
        network.addVertex(node)

        getWorldSave().markDirty()
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

    fun combineNetwork(nodeA: BlockPos, nodeB: BlockPos, networkId_old: UUID, networkId_new: UUID, world: World, type:
    PipeType) {

        val network_old = networks[world.worldInfo.worldName]!![networkId_old]!!
        var network_new = networks[world.worldInfo.worldName]!![networkId_new]!!

        val builder = Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java)
        builder.addGraph(network_old)
        builder.addGraph(network_new)
        network_new = builder.build()
        network_new.addEdge(nodeA, nodeB, WrappedPipeType(type))

        networks[world.worldInfo.worldName]!!.remove(networkId_old)

        val iterator = DepthFirstIterator(network_old, if (network_old.containsVertex(nodeB)) nodeB else nodeA)

        iterator.forEach { firstNode ->
            forceNetworkId(firstNode, networkId_new, world)
        }
    }

    fun splitNetwork(nodeA: BlockPos, networkId: UUID, world: World) {
        val network = networks[world.worldInfo.worldName]!![networkId]!!
        val newNetwork = Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        val newUUID = UUID.randomUUID()
        val iterator = DepthFirstIterator(network, nodeA)
        val removeList = mutableSetOf<BlockPos>()

        iterator.forEach { firstNode ->
            newNetwork.addVertex(firstNode)
            removeList.add(firstNode)

            val edges = network.edgesOf(firstNode)
            edges.forEach(Consumer { edge ->
                val edgetmp = network.getEdgeSource(edge)
                val secondNode = if (edgetmp == firstNode) network.getEdgeTarget(edge) else edgetmp

                newNetwork.addEdge(firstNode, secondNode, edge)
            })
        }
        network.removeAllVertices(removeList)

        networks[world.worldInfo.worldName] = mutableMapOf(Pair(newUUID, newNetwork))

        forceNetworkId(removeList, newUUID, world)
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

    fun getWorldSave(): WorldSavedData {
        val storage = Minecraft.getMinecraft().world.mapStorage!!

        var instance = storage.getOrLoadData(WorldPipeData::class.java, saveName)

        if (instance == null) {
            instance = WorldPipeData(this)
            storage.setData(saveName, instance)
        }

        return instance
    }

    data class WrappedPipeType(val pipeType: PipeType)

    enum class PipeType {
        ENERGY, FLUID, ITEM
    }
}