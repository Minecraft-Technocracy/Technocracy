package net.cydhra.technocracy.foundation.content.tileentities.pipe

import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.cydhra.technocracy.foundation.conduits.ConduitNetwork
import net.cydhra.technocracy.foundation.conduits.parts.AttachmentPart
import net.cydhra.technocracy.foundation.conduits.parts.EdgePart
import net.cydhra.technocracy.foundation.conduits.parts.NodePart
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.content.tileentities.AggregatableTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFacadeComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityPipeTypesComponent
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.IBlockAccess
import net.minecraft.world.WorldServer
import org.lwjgl.util.vector.Matrix3f
import org.lwjgl.util.vector.Vector3f
import kotlin.math.abs


class TileEntityPipe : AggregatableTileEntity() {
    companion object {
        const val boxsize = 0.05
        const val nodeSize = 0.075 * 1.5

        val node = getBB(
            Vec3d((0.5 - nodeSize), (0.5 - nodeSize), (0.5 - nodeSize)),
            Vec3d((0.5 + nodeSize), (0.5 + nodeSize), (0.5 + nodeSize))
        )

        val boxes = mapOf(
            EnumFacing.NORTH to getBB(
                Vec3d((0.5 - boxsize), (0.5 - boxsize), 0.0),
                Vec3d((0.5 + boxsize), (0.5 + boxsize), (0.5 - boxsize))
            ),
            EnumFacing.SOUTH to getBB(
                Vec3d((0.5 - boxsize), (0.5 - boxsize), (0.5 + boxsize)),
                Vec3d((0.5 + boxsize), (0.5 + boxsize), 1.0)
            ),
            EnumFacing.EAST to getBB(
                Vec3d((0.5 + boxsize), (0.5 - boxsize), (0.5 - boxsize)),
                Vec3d(1.0, (0.5 + boxsize), (0.5 + boxsize))
            ),
            EnumFacing.WEST to getBB(
                Vec3d(0.0, (0.5 - boxsize), (0.5 - boxsize)),
                Vec3d((0.5 - boxsize), (0.5 + boxsize), (0.5 + boxsize))
            ),
            EnumFacing.UP to getBB(
                Vec3d((0.5 - boxsize), (0.5 + boxsize), (0.5 - boxsize)),
                Vec3d((0.5 + boxsize), 1.0, (0.5 + boxsize))
            ),
            EnumFacing.DOWN to getBB(
                Vec3d((0.5 - boxsize), 0.0, (0.5 - boxsize)),
                Vec3d((0.5 + boxsize), (0.5 - boxsize), (0.5 + boxsize))
            )
        )


        val connectors: List<AxisAlignedBB>
        val pipeVectors: Map<EnumFacing, Pair<List<List<Vector3f>>, Vector3f>>

        init {
            val pixelSize = 1 / 16f
            val connectorThickness = pixelSize * FacadeBakery.facadeSize.toDouble()
            val connectorRadius = 0.6 / 2

            connectors = listOf(
                AxisAlignedBB(0.0, 0.0, 0.0, 0.0, connectorThickness, 0.0).offset(0.5, 0.0, 0.5)
                    .grow(connectorRadius, 0.0, connectorRadius),
                AxisAlignedBB(0.0, 1.0 - connectorThickness, 0.0, 0.0, 1.0, 0.0).offset(0.5, 0.0, 0.5)
                    .grow(connectorRadius, 0.0, connectorRadius),
                AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, connectorThickness).offset(0.5, 0.5, 0.0)
                    .grow(connectorRadius, connectorRadius, 0.0),
                AxisAlignedBB(0.0, 0.0, 1.0 - connectorThickness, 0.0, 0.0, 1.0).offset(0.5, 0.5, 0.0)
                    .grow(connectorRadius, connectorRadius, 0.0),
                AxisAlignedBB(0.0, 0.0, 0.0, connectorThickness, 0.0, 0.0).offset(0.0, 0.5, 0.5)
                    .grow(0.0, connectorRadius, connectorRadius),
                AxisAlignedBB(1.0 - connectorThickness, 0.0, 0.0, 1.0, 0.0, 0.0).offset(0.0, 0.5, 0.5)
                    .grow(0.0, connectorRadius, connectorRadius)
            )


            //start positions of the cable bundles
            //oriented towards positive z axis
            val boxoffset = 0.1f
            val vectors = mutableListOf(
                listOf(
                    Vector3f(0.0f, 0.0f, 0.0f)
                ), listOf(
                    Vector3f(boxoffset, 0.0f, 0.0f),
                    Vector3f(-boxoffset, 0.0f, 0.0f),
                ), listOf(
                    Vector3f(0.0f, -boxoffset, 0.0f),
                    Vector3f(boxoffset, boxoffset, 0.0f),
                    Vector3f(-boxoffset, boxoffset, 0.0f),
                ), listOf(
                    Vector3f(-boxoffset, -boxoffset, 0.0f),
                    Vector3f(boxoffset, -boxoffset, 0.0f),
                    Vector3f(boxoffset, boxoffset, 0.0f),
                    Vector3f(-boxoffset, boxoffset, 0.0f),
                ), listOf(
                    Vector3f(-boxoffset, -boxoffset, 0.0f),
                    Vector3f(boxoffset, -boxoffset, 0.0f),
                    Vector3f(boxoffset, boxoffset, 0.0f),
                    Vector3f(-boxoffset, boxoffset, 0.0f),
                    Vector3f(0.0f, 0.0f, 0.0f),
                )
            )


            fun EnumFacing.isPos(): Boolean {
                return this.axisDirection == EnumFacing.AxisDirection.POSITIVE
            }

            operator fun Vec3i.invoke(): Vector3f {
                return Vector3f(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())
            }

            fun AxisAlignedBB.offset(vec: Vector3f): AxisAlignedBB {
                return this.offset(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())
            }

            fun AxisAlignedBB.offset(offset: Double): AxisAlignedBB {
                return this.offset(offset, offset, offset)
            }

            pipeVectors = mutableMapOf<EnumFacing, Pair<MutableList<MutableList<Vector3f>>, Vector3f>>().apply {
                for (facing in EnumFacing.values()) {

                    val facingVectorList = mutableListOf<MutableList<Vector3f>>()

                    //if the axis is negative switch the facing and invert the direction vector
                    //this part switches the facing
                    val face = facing.takeIf { it.isPos() } ?: facing.opposite

                    //calc cross product so we know the rotation axis
                    val rot = face.directionVec.crossProduct(EnumFacing.SOUTH.directionVec)()

                    //cos 0
                    //sin 1
                    val matrix = Matrix3f().apply {
                        m00 = rot.x
                        m01 = rot.x * rot.y - rot.z
                        m02 = rot.x * rot.z + rot.y
                        m10 = rot.y * rot.x + rot.z
                        m11 = rot.y
                        m12 = rot.y * rot.z - rot.x
                        m20 = rot.z * rot.x - rot.y
                        m21 = rot.z * rot.y + rot.x
                        m22 = rot.z
                    }

                    //rotate all vectors using the matrix
                    for (vecs in vectors) {
                        facingVectorList.add(mutableListOf<Vector3f>().apply {
                            for (v in vecs) {
                                if (facing.axis == EnumFacing.SOUTH.axis) {
                                    //we are on the z axis so we dont need to rotate the vectors
                                    add(v)
                                } else {
                                    //rotate the location vector
                                    add(Matrix3f.transform(matrix, v, null))
                                }
                            }
                        })
                    }

                    //get the direction vector
                    val direction = if (facing.axis == EnumFacing.SOUTH.axis) {
                        facing.directionVec()
                    } else {
                        //if the axis is negative switch the facing and invert the direction vector
                        //this part inverts the direction vector
                        val dir = if (facing.isPos()) {
                            EnumFacing.SOUTH
                        } else {
                            EnumFacing.NORTH
                        }.directionVec()

                        //also rotate the direction vector using the matrix
                        Matrix3f.transform(matrix, dir, null)
                    }

                    put(facing, facingVectorList to direction)
                }
            }
        }

        private fun getBB(min: Vec3d, max: Vec3d): AxisAlignedBB {
            return AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z)
        }
    }

    private val pipeTypes = TileEntityPipeTypesComponent()
    private val facades = TileEntityFacadeComponent()

    init {
        registerComponent(pipeTypes, "pipeTypes")
        registerComponent(facades, "facades")
    }

    fun addFacadeOnSide(stack: ItemStack, side: EnumFacing): Boolean {
        if (facades.facades[side] != null)
            return false
        val stack = stack.copy()
        stack.count = 1
        facades.facades[side] = stack
        markForUpdate()
        this.world.checkLight(this.pos)
        return true
    }

    fun removeFacadeOnSide(side: EnumFacing): Boolean {
        if (facades.facades[side] != null) {
            facades.facades.remove(side)
            markForUpdate()
            this.world.checkLight(this.pos)
            return true
        }
        return false
    }

    fun getFacades(): Map<EnumFacing, ItemStack> {
        return facades.facades
    }

    fun hasPipeType(type: PipeType): Boolean {
        return pipeTypes.types.contains(type)
    }

    fun addPipeType(type: PipeType) {
        pipeTypes.types.add(type)
        //todo update network pipe tier

        if (!this.world.isRemote) {
            val transaction = ConduitNetwork.beginTransaction()

            ConduitNetwork.addConduitNode(transaction, this.world as WorldServer, this.pos, type)

            EnumFacing.values().forEach { face ->
                val offset = this.pos.offset(face)
                if (ConduitNetwork.hasConduitNode(this.world as WorldServer, offset, type)) {
                    ConduitNetwork.insertConduitEdge(transaction, this.world as WorldServer, this.pos, offset, type)
                }

                if (world.getTileEntity(offset)?.hasCapability(type.capability, face.opposite) == true) {
                    ConduitNetwork.attachTransitSink(transaction, world as WorldServer, pos, face, type)
                }
            }

            transaction.commit()
        }
        markForUpdate()
    }

    fun removePipeType(type: PipeType) {
        if (!this.world.isRemote) {

            pipeTypes.types.remove(type)

            if (pipeTypes.types.isEmpty()) {
                for (f in facades.facades) {
                    f.value.count = 1
                    Block.spawnAsEntity(world, pos, f.value)
                }
                world.setBlockState(this.pos, Blocks.AIR.defaultState)
            }

            val transaction = ConduitNetwork.beginTransaction()
            ConduitNetwork.removeConduitNode(transaction, this.world as WorldServer, this.pos, type)
            ConduitNetwork.removeAllAttachedSinks(transaction, this.world as WorldServer, this.pos, type)

            EnumFacing.values().forEach { face ->
                val offset = this.pos.offset(face)
                if (ConduitNetwork.hasConduitNode(this.world as WorldServer, offset, type)) {
                    ConduitNetwork.removeConduitEdge(transaction, this.world as WorldServer, this.pos, offset, type)
                }
            }

            transaction.commit()
            markForUpdate()
        }
    }

    fun removeTileEntity() {
        if (!this.world.isRemote) {
            PipeType.values().forEach { type ->
                if (ConduitNetwork.hasConduitNode(this.world as WorldServer, this.pos, type)) {
                    val transaction = ConduitNetwork.beginTransaction()

                    ConduitNetwork.removeConduitNode(transaction, this.world as WorldServer, this.pos, type)
                    ConduitNetwork.removeAllAttachedSinks(transaction, this.world as WorldServer, this.pos, type)

                    EnumFacing.values().forEach { face ->
                        val offset = this.pos.offset(face)
                        if (ConduitNetwork.hasConduitNode(this.world as WorldServer, offset, type)) {
                            ConduitNetwork.removeConduitEdge(
                                transaction, this.world as WorldServer,
                                this.pos, offset, type
                            )
                        }
                    }
                    transaction.commit()
                }
            }
        }
    }

    fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        if (!this.world.isRemote) {
            val face = EnumFacing.values().first { pos.offset(it) == neighbor }

            this.pipeTypes.types.forEach { type ->
                val transaction = ConduitNetwork.beginTransaction()
                if (world.getTileEntity(neighbor)?.hasCapability(type.capability, face.opposite) == true) {
                    if (!ConduitNetwork.hasSink(world as WorldServer, pos, face, type)) {
                        ConduitNetwork.attachTransitSink(transaction, world, pos, face, type)
                    }
                } else {
                    if (ConduitNetwork.hasSink(world as WorldServer, pos, face, type)) {
                        ConduitNetwork.removeTransitSink(transaction, world, pos, face, type)
                    }
                }
                transaction.commit()
            }
        }
    }

    fun getInstalledTypes(): List<PipeType> {
        return listOf(*pipeTypes.types.toTypedArray())
    }

    override fun onLoad() {

    }


    enum class BoxType {
        PIPE, BOX, FACADE, CONNECTOR
    }

    /**
     * Returns a list of triples which contain information about the pipe model parts
     * First is a pair of EnumFacing and corresponding bounding box
     * Second is the PipeType
     * Third is an integer indicating which type the model part is (0: node, 1: connection)
     */
    fun getPipeModelParts(): List<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, BoxType>> {
        val out = mutableListOf<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, BoxType>>()

        //todo for the love of god fix on server
        val parts = ConduitNetwork.getNodeParts(
            Minecraft.getMinecraft().integratedServer!!.getWorld(world.provider.dimension),
            this.pos
        ).sortedBy {
            if (it is NodePart)
                return@sortedBy it.pipeType.ordinal
            if (it is EdgePart)
                return@sortedBy it.pipeType.ordinal
            if (it is AttachmentPart)
                return@sortedBy it.pipeType.ordinal
            return@sortedBy 0
        }


        val facings = mutableMapOf<EnumFacing, Int>()
        val hasEdge = mutableSetOf<PipeType>()
        var nodes = 0
        for (p in parts) {
            when (p) {
                is AttachmentPart -> {
                    out.add(Triple(p.facing to connectors[p.facing.index], p.pipeType, BoxType.CONNECTOR))
                }
                is EdgePart -> {
                    val size = facings.getOrDefault(p.facing, 0)
                    facings[p.facing] = size + 1
                    hasEdge.add(p.pipeType)
                }
                is NodePart -> {
                    nodes++
                }
            }
        }

        val facingsCounter = mutableMapOf<EnumFacing, Int>()

        fun MutableMap<EnumFacing, Int>.getAndInc(key: EnumFacing): Int {
            val tmp = facingsCounter.getOrElse(key) {
                facingsCounter[key] = 1
                return 0
            }
            facingsCounter[key] = tmp + 1
            return tmp
        }

        fun AxisAlignedBB.offset(vec: Vector3f): AxisAlignedBB {
            return this.offset(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())
        }

        fun AxisAlignedBB.offset(offset: Double): AxisAlignedBB {
            return this.offset(offset, offset, offset)
        }

        for (p in parts) {
            when (p) {
                is EdgePart -> {
                    val (vecs, dir) = pipeVectors[p.facing]!!
                    val offset = vecs[facings[p.facing]!! - 1][facingsCounter.getAndInc(p.facing)]

                    val x = dir.x * 0.5 + (1 - abs(dir.x)) * nodeSize
                    val y = dir.y * 0.5 + (1 - abs(dir.y)) * nodeSize
                    val z = dir.z * 0.5 + (1 - abs(dir.z)) * nodeSize

                    out.add(
                        Triple(
                            p.facing to AxisAlignedBB(0.0, 0.0, 0.0, x, y, z)
                                .offset(offset).offset(0.5), p.pipeType, BoxType.PIPE
                        )
                    )

                    out.add(
                        Triple(
                            p.facing to AxisAlignedBB(
                                -nodeSize * 0.1,
                                -nodeSize * 0.1,
                                -nodeSize * 0.1,
                                nodeSize + nodeSize * 0.1,
                                nodeSize + nodeSize * 0.1,
                                nodeSize + nodeSize * 0.1
                            ).offset(offset).offset(0.5), p.pipeType, BoxType.CONNECTOR
                        )
                    )
                }
                is AttachmentPart -> {
                }
                is NodePart -> {
                    if (!hasEdge.contains(p.pipeType)) {
                        val expansion = ((this.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2
                        out.add(
                            Triple(
                                EnumFacing.NORTH to node.expand(expansion * 2, 0.0, expansion * 2).offset(
                                    -expansion,
                                    0.0,
                                    -expansion
                                ), if (getInstalledTypes().size == 1) getInstalledTypes().first() else null, BoxType.BOX
                            )
                        )
                    }
                }
            }
        }

        //calculation of the box that hides the bends of the cable

        //calculate the max size of cables per axis
        val bends = mutableMapOf<EnumFacing.Axis, Int>()
        for ((face, int) in facings) {
            bends[face.axis] = (bends[face.axis] ?: 0).coerceAtLeast(int)
        }

        val x = bends[EnumFacing.Axis.X] ?: 0
        val y = bends[EnumFacing.Axis.Y] ?: 0
        val z = bends[EnumFacing.Axis.Z] ?: 0
        val scalar = Vector3f(0.0f, 0.0f, 0.0f)

        //needs to be an edge with atleast 2 cables
        //or a y edge with atleast 3 cables and some horizontal edges
        if (bends.filter { it.value > 0 }.count() >= 2 || (y >= 3 && x + z > 0)) {


            //normal case L junktion of 2 cable bundles
            if ((x >= 2f && z >= 2f)) {
                scalar.x = 1f
                scalar.z = 1f
            }

            //atleast 3 calbes in one direction
            //needs to ajust the height
            if (x >= 3f || z >= 3f || y >= 3f) {
                scalar.y = 1f
            }

            //if the y bundle has atleast 3 cables we need to make it thicker
            if (y >= 3) {
                scalar.x = 1f
                scalar.z = 1f
            }

            //2 cables onto 1 calbe we need to cover that up too
            //===
            // |
            if (x >= 2f && z <= 1f) {
                scalar.z = 1f
            }

            //2 cables onto 1 calbe we need to cover that up too
            //  ||
            //--||
            //  ||
            if (z >= 2f && x <= 1f) {
                scalar.x = 1f
            }
        }

        if (scalar.x > 0f || scalar.y > 0f || scalar.z > 0f) {
            out.add(

                //we need to be a bit bigger then the normal end connector
                //so we use 0.15 as the normal one uses 0.1

                //then we add our scaling in the desired direction
                //scale it with 0.12 as that did visually look good
                Triple(
                    EnumFacing.NORTH to AxisAlignedBB(
                        -nodeSize * 0.15 - scalar.x * 0.12,
                        -nodeSize * 0.15 - scalar.y * 0.12,
                        -nodeSize * 0.15 - scalar.z * 0.12,
                        nodeSize + nodeSize * 0.15 + scalar.x * 0.12,
                        nodeSize + nodeSize * 0.15 + scalar.y * 0.12,
                        nodeSize + nodeSize * 0.15 + +scalar.z * 0.12
                    ).offset(0.5, 0.5, 0.5), null, BoxType.BOX
                )
            )
        }

        //Calc facades

        val pixelSize = 1 / 16f
        val height = pixelSize * FacadeBakery.facadeSize.toDouble()

        this.facades.facades.forEach { (facing, _) ->
            val bb = when (facing) {
                EnumFacing.UP -> {
                    AxisAlignedBB(0.0, 1.0 - height, 0.0, 1.0, 1.0, 1.0)
                }
                EnumFacing.DOWN -> {
                    AxisAlignedBB(0.0, 0.0, 0.0, 1.0, height, 1.0)
                }
                EnumFacing.SOUTH -> {
                    AxisAlignedBB(0.0, 0.0, 1.0 - height, 1.0, 1.0, 1.0)
                }
                EnumFacing.NORTH -> {
                    AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, height)
                }
                EnumFacing.EAST -> {
                    AxisAlignedBB(1.0 - height, 0.0, 0.0, 1.0, 1.0, 1.0)
                }
                EnumFacing.WEST -> {
                    AxisAlignedBB(0.0, 0.0, 0.0, height, 1.0, 1.0)
                }
            }
            out.add(Triple(facing to bb, null, BoxType.FACADE))
        }

        return out
    }
}