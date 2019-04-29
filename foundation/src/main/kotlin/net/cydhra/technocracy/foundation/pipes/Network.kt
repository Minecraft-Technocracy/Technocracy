package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.Multigraph
import java.util.*


class Network {
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
        network.removeVertex(node)

        getWorldSave().markDirty()
    }

    fun addNode(node: BlockPos, networkId: UUID, world: World) {
        networks.getOrPut(world.worldInfo.worldName) {
            mutableMapOf(Pair<UUID, Multigraph<BlockPos, WrappedPipeType>>(UUID.randomUUID(), Multigraph
                    .createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

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