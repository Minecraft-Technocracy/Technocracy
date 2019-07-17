package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.jgrapht.graph.Multigraph
import org.jgrapht.traverse.DepthFirstIterator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object Network {
    var networks = mutableMapOf<String, MutableMap<UUID, Multigraph<WrappedBlockPos, WrappedPipeType>>>()

    var savedData = WorldPipeData(this)

    var dirty = false

    fun loadEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World, type: PipeType) {
        if (world.isRemote) return
        val network = networks[getName(world)]!![networkId]!!
        network.addEdge(WrappedBlockPos(connectToBlock), WrappedBlockPos(newBlockPos), WrappedPipeType(type))
    }

    fun addEdge(newBlockPos: WrappedBlockPos, connectToBlock: WrappedBlockPos, networkId: UUID, world: World,
            type: PipeType) {
        if (world.isRemote) return

        val map = networks.getOrPut(getName(world)) {
            mutableMapOf(Pair(networkId,
                    Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

        val network = map.getOrPut(networkId) {
            Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        }

        network.addVertex(newBlockPos)
        network.addVertex(connectToBlock)

        //check if the current network already has an edge between the nodes with the specific pipe type
        if (network.getAllEdges(connectToBlock, newBlockPos).none { it.pipeType == type }) {
            network.addEdge(connectToBlock, newBlockPos, WrappedPipeType(type))
            dirty = true
        }
    }

    fun addIOToNode(node: BlockPos, facing: EnumFacing, networkId: UUID, world: World, type: PipeType) {
        val node = WrappedBlockPos(node)

        val network = networks[getName(world)]!![networkId]!!

        if (network.containsVertex(node)) {
            //gets the node instance from the network
            val currentNode = network.vertexSet().first { it == node }
            val map = currentNode.io.getOrPut(type) {
                mutableMapOf()
            }

            val set = map.getOrPut(IO.BOTH) {
                mutableSetOf()
            }

            //add IO facing to the node
            set.add(facing)

        }
        dirty = true
    }

    fun removeIOFromNode(node: BlockPos, networkId: UUID, world: World) {
        val node = WrappedBlockPos(node)

        val network = networks[getName(world)]!![networkId]!!

        if (network.containsVertex(node)) {
            //gets the node instance from the network
            val currentNode = network.vertexSet().first { it == node }
            currentNode.io.clear()
        }
    }

    fun rotateIO(node: BlockPos, networkId: UUID, world: World) {
        val node = WrappedBlockPos(node)

        val network = networks[getName(world)]!![networkId]!!

        if (network.containsVertex(node)) {
            //gets the node instance from the network
            val currentNode = network.vertexSet().first { it == node }

            val newIo = mutableMapOf<PipeType, MutableMap<Network.IO, MutableSet<EnumFacing>>>()


            currentNode.io.forEach { pipetype, map ->
                val newmap = mutableMapOf<IO, MutableSet<EnumFacing>>()

                for (pair in map) {
                    newmap[IO.values()[(pair.key.ordinal + 1) % IO.values().size]] = pair.value
                }
                newIo[pipetype] = newmap
            }
            currentNode.io.clear()
            currentNode.io.putAll(newIo)
        }
        dirty = true
    }

    /*fun removeEdge(newBlockPos: BlockPos, connectToBlock: BlockPos, networkId: UUID, world: World, type:
    PipeType) {
        if (world.isRemote)
            return
        val network = networks[world.worldInfo.worldName]!![networkId]!!

        val newBlockPos = WrappedBlockPos(newBlockPos)
        val connectToBlock = WrappedBlockPos(connectToBlock)

        val edge = network.getAllEdges(newBlockPos, connectToBlock).find { it.pipeType == type }
        network.removeEdge(edge)

        dirty = true
    }*/

    /*@Deprecated("use new method")
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

        network.removeVertex(WrappedBlockPos(node))

        if (network.vertexSet().size == 0) {
            networks[world.worldInfo.worldName]!!.remove(networkId)
        }

        dirty = true
    }*/

    private fun removeNode(node: WrappedBlockPos, networkId: UUID, world: World) {
        if (world.isRemote) return
        val network = networks[getName(world)]!![networkId]!!

        network.removeVertex(node)

        //if network is empty remove it
        if (network.vertexSet().isEmpty()) {
            networks[getName(world)]!!.remove(networkId)
        }

        dirty = true
    }

    /**
     * Remove the [node] from every network that is inside the world [world]
     *
     * @param node the node that is beeing removed
     * @param world the world of the node
     */
    fun removeNodeInEveryNetwork(node: BlockPos, world: World) {
        if (world.isRemote) return

        val newNetworks = mutableMapOf<UUID, Multigraph<WrappedBlockPos, WrappedPipeType>>()

        val node = WrappedBlockPos(node)

        //loop every world
        mapOf(*networks.toList().toTypedArray()).forEach { _, worldNetworks ->
            //loop every network in the world
            mapOf(*worldNetworks.toList().toTypedArray()).forEach { id, network ->
                if (network.containsVertex(node)) {
                    //get a copy of all the edges from the node
                    val edges = setOf(*network.edgesOf(node).toTypedArray())

                    //remove the node from the network
                    network.removeVertex(node)

                    //if node has more then one edge, split the networks
                    if (edges.size > 1) {
                        val used = mutableSetOf<WrappedBlockPos>()
                        val remove = mutableSetOf(node)

                        //loop all edges
                        edges.forEachIndexed { index, wrappedPipeType ->

                            //get the node
                            val currentNode =
                                    if (wrappedPipeType.getSourceNode() == node) wrappedPipeType.getTargetNode() else wrappedPipeType.getSourceNode()

                            if (index == 0) {
                                //iterate all connected nodes, they keep the network id
                                val iterator = DepthFirstIterator(network, currentNode)
                                iterator.forEach { used.add(it) }
                            } else {
                                //check if the node was already cached
                                if (!used.contains(currentNode)) {
                                    //new network branch found that is not connected to the existing network
                                    val iterator = DepthFirstIterator(network, currentNode)

                                    //create new network
                                    val newNetwork =
                                            Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java)
                                                    .build()
                                    val uuid = UUID.randomUUID()
                                    newNetworks[uuid] = newNetwork

                                    //copy all connected nodes and edges to the net network
                                    iterator.forEach {
                                        used.add(it)
                                        remove.add(it)
                                        newNetwork.addVertex(it)
                                        //update network id to the new one
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

                        //remove all nodes from the original network, that are now in a new network
                        for (rem in remove) {
                            removeNode(rem, id, world)
                        }
                    }

                    //remove network if it is empty
                    if (network.vertexSet().isEmpty()) {
                        networks[getName(world)]!!.remove(id)
                    }
                }
            }
        }

        //add all new network to the world
        networks[getName(world)]!!.putAll(newNetworks)
        dirty = true
    }

    /**
     * Add a note to a network
     *
     * @param node the new node
     * @param networkId the id of the network that the node will be added
     * @param world the world in which the network is in
     */
    fun addNode(node: WrappedBlockPos, networkId: UUID, world: World) {
        if (world.isRemote) return

        val map = networks.getOrPut(getName(world)) {
            mutableMapOf(Pair(networkId,
                    Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

        val network = map.getOrPut(networkId) {
            Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        }

        network.addVertex(node)
        dirty = true
    }

    /**
     * Load a node into a network without it beeing saved to the disk
     *
     * @param node the new node
     * @param networkId the id of the network that the node will be added
     * @param world the world in which the network is in
     */
    fun loadNode(node: WrappedBlockPos, networkId: UUID, world: World) {
        if (world.isRemote) return
        val map = networks.getOrPut(getName(world)) {
            mutableMapOf(Pair(networkId,
                    Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()))
        }

        val network = map.getOrPut(networkId) {
            Multigraph.createBuilder<WrappedBlockPos, WrappedPipeType>(WrappedPipeType::class.java).build()
        }

        if (!network.vertexSet().contains(node)) {
            network.addVertex(node)
        }
    }

    /**
     * Combine two nodes from two networks
     */
    fun combineNetwork(nodeA: WrappedBlockPos, nodeB: WrappedBlockPos, networkId_old: UUID, networkId_new: UUID,
            world: World, type: PipeType) {
        if (world.isRemote) return
        val network_old = networks[getName(world)]!![networkId_old]!!
        val network_new = networks[getName(world)]!![networkId_new]!!

        network_old.vertexSet().forEach {
            network_new.addVertex(it)
            forceNetworkId(it, networkId_new, world)
        }
        network_old.edgeSet().forEach { network_new.addEdge(it.getSourceNode(), it.getTargetNode(), it) }
        network_new.addEdge(nodeA, nodeB, WrappedPipeType(type))

        networks[getName(world)]!!.remove(networkId_old)

        dirty = true
    }

    /**
     * Forces the tileentity to update to the given network id
     */
    fun forceNetworkId(block: WrappedBlockPos, uuid: UUID, world: World) {
        val tile = world.getTileEntity(block.pos)
        if (tile != null && tile is TileEntityPipe) {
            tile.setNetworkId(uuid)
        }
    }

    private fun getName(world: World): String {
        return world.provider.dimensionType.getName() + "_" + world.provider.dimension
    }

    @SubscribeEvent
    fun initWorld(e: WorldEvent.Load) {
        if (!e.world.isRemote) {
            val file =
                    File(DimensionManager.getCurrentSaveRootDirectory()!!.absolutePath + "./data/${TCFoundation.MODID}/pipes.dat")
            if (file.exists()) savedData.readNBT(e.world, CompressedStreamTools.readCompressed(FileInputStream(file)))
        }
    }

    @SubscribeEvent
    fun unloadWorld(e: WorldEvent.Unload) {
        if (!e.world.isRemote) {
            networks.remove(getName(e.world))
        }
    }

    @SubscribeEvent
    fun worldSave(e: WorldEvent.Save) {
        if (!e.world.isRemote && dirty) {
            dirty = false
            println("Saving Network")
            val file =
                    File(DimensionManager.getCurrentSaveRootDirectory()!!.absolutePath + "./data/${TCFoundation.MODID}/pipes.dat")
            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            CompressedStreamTools.writeCompressed(savedData.writeNBT(), FileOutputStream(file))
        }
    }

    @SubscribeEvent
    fun tick(tick: TickEvent.WorldTickEvent) {
        if (tick.side.isServer && tick.phase == TickEvent.Phase.START) {
            val worldnetworks = networks[getName(tick.world)]

            if (worldnetworks != null) {
                for (graph in worldnetworks.values) {
                    val endpoints = graph.vertexSet().filter { it.hasIO }
                    endpoints.forEach {
                        for (pipeType in PipeType.values()) {
                            //active extraction, so only tick on strict output nodes
                            val outputs = it.getOutputFacings(pipeType, true)
                            for (extractSide in outputs) {

                                val extractionPipe = tick.world.getTileEntity(it.pos) as TileEntityPipe
                                val extractionTile = tick.world.getTileEntity(it.pos.offset(extractSide))!!

                                pipeType.handler.handle(tick.world,
                                        it,
                                        extractionPipe,
                                        extractionTile,
                                        extractSide,
                                        FilteredPipeTypeGraph(graph, pipeType))
/*


                                val iterator = ClosestFirstIterator(FilteredPipeTypeGraph(graph, pipeType), it)

                                //output should not be split equal to all inputs, just find the best exit node
                                if (!pipeType.splitInputEqual) {
                                    iterator@ for (node in iterator) {
                                        if (!node.hasIO) continue
                                        val inputs = node.getInputFacings(pipeType, false)
                                        for (inputSide in inputs) {
                                            if (node == it && outputSide == inputSide) continue

                                            val pipeIn = tick.world.getTileEntity(node.pos) as TileEntityPipe
                                            val tileIn = tick.world.getTileEntity(node.pos.offset(inputSide))!!

                                            if (pipeType.canDoAction(pipeType,
                                                            pipeOut,
                                                            pipeIn,
                                                            tileOut,
                                                            tileIn,
                                                            outputSide,
                                                            inputSide)) {
                                                //TODO timeout

                                                println("found output")

                                                break@iterator
                                            }
                                        }
                                    }
                                }*/
                            }
                        }
                    }
                }
            }
        }
    }


    /*enum class PipeTier(val type: PipeType, weight: Double) {
        @Deprecated("Internal use only")
        NONE(PipeType.NONE, 0.0),
        BASIC_ENERGY(PipeType.ENERGY, 0.0), BASIC_FLUID(PipeType.FLUID, 0.0), BASIC_ITEM(PipeType.ITEM, 0.0);

        companion object {
            val basicTiers = setOf(BASIC_ENERGY, BASIC_FLUID, BASIC_ITEM)

            fun getBasicTier(type: PipeType): PipeTier {
                return basicTiers.find { it.type == type }!!
            }
        }

    }*/

    enum class IO {
        INPUT, OUTPUT, BOTH
    }
}