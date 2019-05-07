package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import org.jgrapht.graph.Multigraph
import org.jgrapht.traverse.DepthFirstIterator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object Network {
    var networks = mutableMapOf<String, MutableMap<UUID, Multigraph<BlockPos, WrappedPipeType>>>()

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

        val map = networks.getOrPut(world.worldInfo.worldName) {
            mutableMapOf(Pair(networkId, Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

        val network = map.getOrPut(networkId) {
            Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        }

        network.addVertex(newBlockPos)
        network.addVertex(connectToBlock)

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

    private fun removeNode(node: BlockPos, networkId: UUID, world: World) {
        if (world.isRemote)
            return
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        /*val edges = network.edgesOf(node)
        if (edges.size > 1) {
            edges.forEach { edge ->
                val edgetmp = network.getEdgeSource(edge)
                val secondNode = if (edgetmp == node) network.getEdgeTarget(edge) else edgetmp
                //splitNetwork(secondNode, networkId, world)
            }
        }*/

        network.removeVertex(node)

        if (network.vertexSet().size == 0) {
            networks[world.worldInfo.worldName]!!.remove(networkId)
        }

        dirty = true
    }

    fun removeNodeInEveryNetwork(node: BlockPos, world: World) {
        if (world.isRemote)
            return

        val newNetworks = mutableMapOf<UUID, Multigraph<BlockPos, WrappedPipeType>>()

        mapOf(*networks.toList().toTypedArray())
                .forEach { _, worldNetworks ->
                    mapOf(*worldNetworks.toList().toTypedArray()).forEach { id, network ->
                        if (network.containsVertex(node)) {
                            val edges = setOf(*network.edgesOf(node).toTypedArray())

                            network.removeVertex(node)

                            if (edges.size > 1) {
                                val used = mutableSetOf<BlockPos>()
                                val remove = mutableSetOf(node)
                                edges.forEachIndexed { index, wrappedPipeType ->

                                    val currentNode =
                                            (if (wrappedPipeType.getSourceNode() == node)
                                                wrappedPipeType.getTargetNode()
                                            else wrappedPipeType.getSourceNode())

                                    if (index == 0) {
                                        val iterator = DepthFirstIterator(network, currentNode)
                                        iterator.forEach { used.add(it) }
                                    } else {
                                        if (!used.contains(currentNode)) {
                                            val iterator = DepthFirstIterator(network, currentNode)
                                            val newNetwork = Multigraph.createBuilder<BlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
                                            val uuid = UUID.randomUUID()
                                            newNetworks[uuid] = newNetwork
                                            iterator.forEach {
                                                used.add(it)
                                                remove.add(it)
                                                newNetwork.addVertex(it)
                                                forceNetworkId(it, uuid, world)

                                                val innerEdge = network.edgesOf(it)
                                                for (e in innerEdge) {
                                                    newNetwork.addVertex(e.getSourceNode())
                                                    newNetwork.addVertex(e.getTargetNode())
                                                    newNetwork.addEdge(e.getSourceNode(), e.getTargetNode(), e)
                                                }
                                            }
                                        }
                                    }
                                }

                                for (rem in remove) {
                                    removeNode(rem, id, world)
                                }
                            }

                            if (network.vertexSet().size == 0) {
                                networks[world.worldInfo.worldName]!!.remove(id)
                            }
                        }
                    }
                }


        networks[world.worldInfo.worldName]!!.putAll(newNetworks)
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

        network.addVertex(node)
        dirty = true
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

    fun combineNetwork(nodeA: BlockPos, nodeB: BlockPos, networkId_old: UUID, networkId_new: UUID, world: World, type:
    PipeType) {
        if (world.isRemote)
            return
        val network_old = networks[world.worldInfo.worldName]!![networkId_old]!!
        val network_new = networks[world.worldInfo.worldName]!![networkId_new]!!

        network_old.vertexSet().forEach {
            network_new.addVertex(it)
            forceNetworkId(it, networkId_new, world)
        }
        network_old.edgeSet().forEach { network_new.addEdge(it.getSourceNode(), it.getTargetNode(), it) }
        network_new.addEdge(nodeA, nodeB, WrappedPipeType(type))

        networks[world.worldInfo.worldName]!!.remove(networkId_old)

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
            val file = File(DimensionManager.getCurrentSaveRootDirectory()!!.absolutePath + "./data/${TCFoundation.MODID}/pipes.dat")
            if (file.exists())
                savedData.readNBT(e.world, CompressedStreamTools.readCompressed(FileInputStream(file)))
        }
    }

    @SubscribeEvent
    fun worldSave(e: WorldEvent.Save) {
        if (!e.world.isRemote && dirty) {
            dirty = false
            println("Saving Network")
            val file = File(DimensionManager.getCurrentSaveRootDirectory()!!.absolutePath + "./data/${TCFoundation.MODID}/pipes.dat")
            if(!file.parentFile.exists())
                file.parentFile.mkdirs()
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

    enum class PipeType(val unlocalizedName: String) : IStringSerializable {
        ENERGY("energy"), FLUID("fluid"), ITEM("item");
        override fun getName(): String {
            return this.unlocalizedName
        }
    }
}