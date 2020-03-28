package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.conduits.transit.TransitChunkEdge
import net.cydhra.technocracy.foundation.conduits.transit.TransitEdge
import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.cydhra.technocracy.foundation.conduits.types.PipeContent
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.world.ChunkDataEvent
import java.util.*

internal class ConduitNetworkDimension(private val dimensionId: Int) {

    companion object {
        private const val NBT_KEY_NETWORK_DATA = "conduits"
    }

    /**
     * A map of all chunks currently loaded
     */
    private val loadedChunks: MutableMap<ChunkPos, ConduitNetworkChunk> = mutableMapOf()

    /**
     * A map of chunks marked for removal
     */
    private val markedForRemoval: MutableMap<ChunkPos, ConduitNetworkChunk> = mutableMapOf()


    /**
     * Called when a chunk loads so the chunk can be parsed and inserted into the dimension network.
     *
     * @param chunk the newly loaded chunk
     */
    internal fun loadChunk(chunk: Chunk) {
        loadedChunks[chunk.pos] = ConduitNetworkChunkDataCache.popChunkData(chunk.pos) ?: ConduitNetworkChunk(chunk.pos)
    }

    /**
     * Called when a chunk is unloaded, so the chunk can be removed from the dimension network. Note, that the
     * [ConduitNetworkChunk] does not necessarily goes out of scope immediately.
     *
     * @param chunk the chunk that is being unloaded
     */
    internal fun unloadChunk(chunk: Chunk) {
        markedForRemoval[chunk.pos] = loadedChunks.remove(chunk.pos)!!
    }

    /**
     * @param pos a chunk position within this dimension
     *
     * @return the chunk at the given position or null, if that chunk is not loaded
     */
    internal fun getChunkAt(pos: ChunkPos): ConduitNetworkChunk? {
        return loadedChunks[pos]
    }

    /**
     * Save all conduit data to the chunk's NBT data
     */
    fun saveChunkData(event: ChunkDataEvent.Save) {
        val chunk = markedForRemoval.remove(event.chunk.pos) ?: this.getChunkAt(event.chunk.pos)!!
        event.data.setTag(NBT_KEY_NETWORK_DATA, chunk.serializeNBT())
    }

    /**
     * Read conduit data from provided NBT data and apply it to a new instance of [ConduitNetworkChunk]. The chunk is
     * then pushed into the chunk data cache, so it can be retrieved whenever the chunk is actually loaded through
     * [loadChunk]
     */
    fun loadChunkData(event: ChunkDataEvent.Load) {
        val chunkData = ConduitNetworkChunk(event.chunk.pos)
        chunkData.deserializeNBT(event.data.getCompoundTag(NBT_KEY_NETWORK_DATA))

        ConduitNetworkChunkDataCache.enqueueChunkData(event.chunk.pos, chunkData)
    }


    internal fun debug_getChunks(): Collection<ConduitNetworkChunk> {
        return this.loadedChunks.values
    }

    /**
     * Tick the network within this dimension.
     */
    fun tick(world: WorldServer) {
        val transitGraph = this.loadedChunks
                .map { (_, chunk) -> chunk.getTransitEdges().map { edge -> edge to chunk } }
                .flatten()

        transitGraph
                .filter { (edge, _) -> edge is TransitSink }
                .forEach { (edge, _) -> (edge as TransitSink).tick() }

        transitGraph
                .filter { (edge, _) -> edge is TransitSink }
                .filter { (edge, _) -> (edge as TransitSink).offersContent(world) }
                .forEach { (source, chunk) ->
                    // get offered content of source
                    val content = (source as TransitSink).getContent(world)

                    // find available sink using routing strategy
                    val potentialTargets = dijkstra(world, source, chunk, content, emptyMap(), true)

                    // transfer content
                    if (potentialTargets.isNotEmpty()) {
                        var remainingContent = content
                        var index = 0

                        while (!remainingContent.isEmpty() && index < potentialTargets.size) {
                            remainingContent = potentialTargets[index++].transferContent(world, remainingContent)
                        }

                        source.setCoolDown()
                    }
                }
    }

    /**
     * @param world the world this algorithm runs in
     * @param start the starting transit edge
     * @param chunk the starting chunk
     * @param content the content to transfer
     * @param usedFlows a map of transit edges that are already used by transfers. There might still be capacity left
     * @param multipleSinks if false, only zero or one sink are returned, otherwise all sinks that accept the content
     * are returned
     */
    fun dijkstra(
            world: WorldServer,
            start: TransitSink,
            chunk: ConduitNetworkChunk,
            content: PipeContent,
            usedFlows: Map<TransitEdge, Int>,
            multipleSinks: Boolean = true
    ): List<TransitSink> {
        // nodes that were already visited
        val visited = mutableListOf<TransitEdge>()

        // priority queue offering the next lowest cost node to visit
        val nodeQueue = PriorityQueue<Triple<ConduitNetworkChunk, TransitEdge, Int>>(kotlin.Comparator { path1, path2 ->
            path1.third.compareTo(path2.third)
        })

        // sinks available from starting point
        val availableSinks = mutableListOf<TransitSink>()

        /**
         * Try to add a transit path into the algorithm, if it has not been used before and has capacity left
         */
        fun enqueuePath(start: TransitEdge, target: TransitEdge, targetChunk: ConduitNetworkChunk, cost: Int) {
            if (visited.contains(target))
                return

            val pathRepresentation = minOf(start.id, target.id) to maxOf(start.id, target.id)

            // TODO check whether the path has left capacity

            nodeQueue.add(Triple(targetChunk, target, cost))
        }

        // enqueue start node
        nodeQueue.add(Triple(chunk, start, 0))

        while (nodeQueue.isNotEmpty()) {
            val (currentChunk, currentEdge, currentCost) = nodeQueue.remove()
            visited += currentEdge

            if (currentEdge is TransitSink) {
                if (currentEdge.acceptsContent(world, content) && currentEdge != start) {
                    availableSinks += currentEdge

                    if (!multipleSinks)
                        return availableSinks
                }
            }

            currentEdge.paths.forEach { (targetId, cost) ->
                val target = currentChunk.getTransitEdge(targetId)!!.second
                enqueuePath(start, target, currentChunk, currentCost + cost)
            }

            if (currentEdge is TransitChunkEdge) {
                val targetPosition = currentEdge.pos.offset(currentEdge.facing)
                val targetChunk = getChunkAt(ChunkPos(targetPosition))

                if (targetChunk != null) {
                    val targetEdge = targetChunk
                            .getTransitChunkEdge(targetPosition, start.type, currentEdge.facing.opposite)
                    enqueuePath(currentEdge, targetEdge!!, targetChunk, currentCost + 1)
                }
            }

        }

        return availableSinks
    }
}