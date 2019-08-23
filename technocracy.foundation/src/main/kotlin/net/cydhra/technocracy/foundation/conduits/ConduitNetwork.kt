package net.cydhra.technocracy.foundation.conduits

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Disk
import org.lwjgl.util.glu.Sphere

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
    fun addConduitNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.insertNode(pos, type)
    }

    /**
     * Remove a node from the conduit network. All edges from and to the node are removed as well. If the node does
     * not exist, an [IllegalStateException] is thrown.
     *
     * @param world world server that removes the node
     * @param pos position in world where the node was removed
     * @param type pipe type that was removed.
     *
     * @throws IllegalStateException if the node does not exist
     * @throws IllegalStateException if the respective chunk is not loaded
     */
    fun removeConduitNode(world: WorldServer, pos: BlockPos, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.removeNode(pos, type)
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
    fun insertConduitEdge(world: WorldServer, nodeA: BlockPos, nodeB: BlockPos, type: PipeType) {
        val directionFromA = EnumFacing.values().firstOrNull { nodeA.add(it.directionVec) == nodeB }
                ?: throw IllegalArgumentException("the positions are not adjacent")

        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")

        val chunkA = dimension.getChunkAt(ChunkPos(nodeA))
                ?: throw IllegalStateException("the chunk of nodeA is not loaded")
        chunkA.insertEdge(nodeA, directionFromA, type)

        val chunkB = dimension.getChunkAt(ChunkPos(nodeB))
                ?: throw IllegalStateException("the chunk of nodeB is not loaded")
        chunkB.insertEdge(nodeB, directionFromA.opposite, type)
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
    fun removeConduitEdge(world: WorldServer, nodeA: BlockPos, nodeB: BlockPos, type: PipeType) {
        val directionFromA = EnumFacing.values().firstOrNull { nodeA.add(it.directionVec) == nodeB }
                ?: throw IllegalArgumentException("the positions are not adjacent")

        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")

        val chunkA = dimension.getChunkAt(ChunkPos(nodeA))
                ?: throw IllegalStateException("the chunk of nodeA is not loaded")
        chunkA.removeEdge(nodeA, directionFromA, type)

        val chunkB = dimension.getChunkAt(ChunkPos(nodeB))
                ?: throw IllegalStateException("the chunk of nodeB is not loaded")
        chunkB.removeEdge(nodeB, directionFromA.opposite, type)
    }

    /**
     * @param world the world object where to look for the pipe
     * @param pos the queried block position
     * @param type the pipe to look for
     *
     * @return true if the conduit network has a node of given type at given block position
     */
    fun hasConduitNode(world: WorldServer, pos: BlockPos, type: PipeType): Boolean {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")
        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        return chunk.hasNode(pos, type)
    }

    fun attachTransitSink(world: WorldServer, pos: BlockPos, facing: EnumFacing, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.insertEdge(pos, facing, type)
        chunk.attachTransitSink(pos, facing, type)
    }

    fun removeTransitSink(world: WorldServer, pos: BlockPos, facing: EnumFacing, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.removeEdge(pos, facing, type)
        chunk.removeTransitSink(pos, facing, type)
    }

    fun removeAllAttachedSinks(world: WorldServer, pos: BlockPos, type: PipeType) {
        val dimension = dimensions[world.provider.dimension]
                ?: throw IllegalStateException("the dimension is not loaded")

        val chunk = dimension.getChunkAt(ChunkPos(pos)) ?: throw IllegalStateException("the chunk is not loaded")

        chunk.removeAllSinks(pos, type)
    }

    /**
     * When a chunk is read from NBT, it has been loaded before and might have conduit data written to it. That must
     * be retrieved
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkDataLoad(event: ChunkDataEvent.Load) {
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId] ?: ConduitNetworkDimension(dimensionId).apply {
            dimensions[dimensionId] = this
        }

        dimension.loadChunkData(event)
    }

    /**
     * When a chunk is being saved to NBT, but does not exist within the network, it is being generated. We can add
     * it to the list of loaded chunks as it is being loaded obviously.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkDataSave(event: ChunkDataEvent.Save) {
        if (event.world.isRemote)
            return

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
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId] ?: ConduitNetworkDimension(dimensionId).apply {
            dimensions[dimensionId] = this
        }

        dimension.loadChunk(event.chunk)
    }

    /**
     * When a chunk gets unloaded, remove it from the dimension.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onChunkUnload(event: ChunkEvent.Unload) {
        if (event.world.isRemote)
            return

        val dimensionId = event.world.provider.dimension
        val dimension = dimensions[dimensionId]!!

        dimension.unloadChunk(event.chunk)
    }

    @SubscribeEvent
    fun renderDebugEvent(event: RenderWorldLastEvent) {
        val mc = Minecraft.getMinecraft()
        val doubleX = mc.player.posX
        val doubleY = mc.player.posY
        val doubleZ = mc.player.posZ

        GL11.glPushMatrix()
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ)


        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_LIGHTING)

        GL11.glDepthMask(false)

        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        GL11.glLineWidth(1f)

        try {
            dimensions[0]!!.debug_getChunks().forEach { nChunk ->
                nChunk.debug_nodes.forEach { (pos, types) ->
                    GL11.glPushMatrix()
                    GL11.glTranslated(pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5)

                    types.forEach { type ->
                        GL11.glPushMatrix()
                        GL11.glTranslated(0.0, 0.2 * type.ordinal, 0.0)
                        when (type) {
                            PipeType.ENERGY -> GL11.glColor3d(1.0, 0.0, 0.0)
                            PipeType.ITEM -> GL11.glColor3d(0.0, 1.0, 0.0)
                            PipeType.FLUID -> GL11.glColor3d(0.0, 0.0, 1.0)
                        }
                        Sphere().draw(0.1f, 16, 16)
                        GL11.glPopMatrix()
                    }
                    GL11.glPopMatrix()
                }

                nChunk.debug_edges.forEach { (pos, map) ->
                    GL11.glPushMatrix()
                    GL11.glTranslated(pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5)

                    map.forEach { (type, facings) ->
                        GL11.glPushMatrix()
                        GL11.glTranslated(0.0, 0.2 * type.ordinal, 0.0)

                        when (type) {
                            PipeType.ENERGY -> GL11.glColor3d(1.0, 0.0, 0.0)
                            PipeType.ITEM -> GL11.glColor3d(0.0, 1.0, 0.0)
                            PipeType.FLUID -> GL11.glColor3d(0.0, 0.0, 1.0)
                        }

                        GL11.glBegin(GL11.GL_LINES)
                        facings.forEach { face ->
                            GL11.glVertex3d(0.0, 0.0, 0.0)
                            GL11.glVertex3d(face.directionVec.x / 2.0,
                                    face.directionVec.y / 2.0, face.directionVec.z / 2.0)

                        }
                        GL11.glEnd()
                        GL11.glPopMatrix()
                    }

                    GL11.glPopMatrix()
                }

                nChunk.debug_sinks.forEach { (pos, map) ->
                    GL11.glPushMatrix()
                    GL11.glTranslated(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5)

                    map.forEach { (type, faces) ->
                        when (type) {
                            PipeType.ENERGY -> GL11.glColor3d(1.0, 0.0, 0.0)
                            PipeType.ITEM -> GL11.glColor3d(0.0, 1.0, 0.0)
                            PipeType.FLUID -> GL11.glColor3d(0.0, 0.0, 1.0)
                        }

                        faces.forEach { face ->
                            GL11.glPushMatrix()
                            when (face) {
                                EnumFacing.DOWN -> GL11.glRotated(90.0, -1.0, 0.0, 0.0)
                                EnumFacing.UP -> GL11.glRotated(90.0, 1.0, 0.0, 0.0)
                                EnumFacing.NORTH -> GL11.glRotated(0.0, 0.0, 1.0, 0.0)
                                EnumFacing.SOUTH -> GL11.glRotated(180.0, 0.0, -1.0, 0.0)
                                EnumFacing.WEST -> GL11.glRotated(90.0, 0.0, 1.0, 0.0)
                                EnumFacing.EAST -> GL11.glRotated(90.0, 0.0, -1.0, 0.0)
                            }
                            Disk().draw(0.01f, 0.3f, 16, 4)
                            GL11.glPopMatrix()
                        }
                    }

                    GL11.glPopMatrix()
                }
            }
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        GL11.glDepthMask(true)
        GL11.glPopMatrix()
    }
}