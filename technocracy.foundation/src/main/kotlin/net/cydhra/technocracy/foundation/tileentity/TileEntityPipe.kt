package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.blocks.general.pipe
import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.cydhra.technocracy.foundation.conduits.ConduitNetwork
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.components.ComponentFacade
import net.cydhra.technocracy.foundation.tileentity.components.ComponentPipeTypes
import net.cydhra.technocracy.foundation.tileentity.components.NetworkComponent
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.world.WorldServer


class TileEntityPipe(meta: Int = -1) : AggregatableTileEntity() {
    companion object {
        const val size = 0.05
        const val nodeSize = 0.075

        val node = AxisAlignedBB(Vec3d((0.5 - nodeSize), (0.5 - nodeSize), (0.5 - nodeSize)),
                Vec3d((0.5 + nodeSize), (0.5 + nodeSize), (0.5 + nodeSize)))

        val connections = mapOf(EnumFacing.NORTH to AxisAlignedBB(Vec3d((0.5 - size), (0.5 - size), 0.0),
                Vec3d((0.5 + size), (0.5 + size), (0.5 - size))),
                EnumFacing.SOUTH to AxisAlignedBB(Vec3d((0.5 - size), (0.5 - size), (0.5 + size)),
                        Vec3d((0.5 + size), (0.5 + size), 1.0)),
                EnumFacing.EAST to AxisAlignedBB(Vec3d((0.5 + size), (0.5 - size), (0.5 - size)),
                        Vec3d(1.0, (0.5 + size), (0.5 + size))),
                EnumFacing.WEST to AxisAlignedBB(Vec3d(0.0, (0.5 - size), (0.5 - size)),
                        Vec3d((0.5 - size), (0.5 + size), (0.5 + size))),
                EnumFacing.UP to AxisAlignedBB(Vec3d((0.5 - size), (0.5 + size), (0.5 - size)),
                        Vec3d((0.5 + size), 1.0, (0.5 + size))),
                EnumFacing.DOWN to AxisAlignedBB(Vec3d((0.5 - size), 0.0, (0.5 - size)),
                        Vec3d((0.5 + size), (0.5 - size), (0.5 + size))))
    }

    private val networkComponent = NetworkComponent()
    private val pipeTypes = ComponentPipeTypes()
    private val facades = ComponentFacade()

    init {
        registerComponent(networkComponent, "network")
        registerComponent(pipeTypes, "pipeTypes")
        registerComponent(facades, "facades")

        if (meta > -1)
            pipeTypes.types.add(PipeType.values()[meta])
    }

    fun addFacadeOnSide(stack: ItemStack, side: EnumFacing): Boolean {
        if (facades.facades[side] != null)
            return false
        facades.facades[side] = stack
        markForUpdate()
        this.world.checkLight(this.pos)
        return true
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
            ConduitNetwork.addConduitNode(this.world as WorldServer, this.pos, type)
        }
        markForUpdate()
    }

    fun getInstalledTypes(): List<PipeType> {
        return listOf(*pipeTypes.types.toTypedArray())
    }

    override fun onLoad() {

    }

    /**
     * Returns a list of triples which contain information about the pipe model parts
     * First is a pair of EnumFacing and corresponding bounding box
     * Second is the PipeType
     * Third is an integer indicating which type the model part is (0: node, 1: connection)
     */
    fun getPipeModelParts(): List<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, Int>> {
        val boxes = mutableListOf<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, Int>>()

        //populate connected facings
        val facings = mutableSetOf<EnumFacing>()
        this.getInstalledTypes().sorted().forEach { type ->
            connections.forEach { (facing, _) ->
                if (world.getBlockState(this.pos.offset(facing)).block == pipe) {
                    val neighbourPipe = (world.getTileEntity(this.pos.offset(facing)) as TileEntityPipe)
                    val connected = neighbourPipe.getInstalledTypes().contains(type)

                    if (connected) {
                        facings.add(facing)
                    }
                }
            }
        }

        //distinguish multiple entries using a set and decide if a connection is straight or goes around a corner
        val straight = facings.map { it.axis }.toSet().size == 1

        //Render all nodes and connections
        this.getInstalledTypes().sorted().forEachIndexed { index, type ->
            connections.forEach { (facing, boundingBox) ->
                var nodeConnectionOffset =
                        (index * node.averageEdgeLength) - ((this.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2

                //The block is a pipe
                if (world.getBlockState(this.pos.offset(facing)).block == pipe) {
                    val neighbourPipe = (world.getTileEntity(this.pos.offset(facing)) as TileEntityPipe)
                    val connected = neighbourPipe.getInstalledTypes().contains(type)

                    //Is connected in any way to the neighbour pipe
                    if (connected) {

                        //neighbour has less types
                        if (neighbourPipe.getInstalledTypes().size < this.getInstalledTypes().size) {
                            //use offset of the neighbour
                            nodeConnectionOffset =
                                    if (this.getInstalledTypes().size != 1 && neighbourPipe.getInstalledTypes().size != 1) { // if both type sizes are not one some more calculations are needed
                                        var neighbourIndex = index

                                        //find the index of the neighbour pipe of the current type
                                        neighbourPipe.getInstalledTypes().sorted().forEachIndexed { idx, it ->
                                            if (it == type) {
                                                neighbourIndex = idx
                                            }
                                        }

                                        //apply the the offset with the current neighbour index
                                        (neighbourIndex * node.averageEdgeLength) - ((neighbourPipe.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2
                                    } else {
                                        ((neighbourPipe.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2 //Apply offset for one installed type
                                    }
                        } else if (neighbourPipe.getInstalledTypes().size == this.getInstalledTypes().size) { //if has same size of types
                            var sameTypeCount = 0

                            //calculate the amount of types both lists have
                            neighbourPipe.getInstalledTypes().sorted().forEach {
                                if (this.getInstalledTypes().contains(it)) {
                                    sameTypeCount++
                                }
                            }

                            if (sameTypeCount == 1) { //both have exactly one same type
                                nodeConnectionOffset = 0.0 //no offset is needed at all
                            }
                        }

                        //Add connection
                        boxes.add(Triple(facing to when {
                            facing.axis == EnumFacing.Axis.X -> boundingBox.offset(0.0, 0.0, nodeConnectionOffset)
                            facing.axis == EnumFacing.Axis.Z -> boundingBox.offset(-nodeConnectionOffset, 0.0, 0.0)
                            facing.axis.isVertical -> boundingBox.offset(0.0, 0.0, nodeConnectionOffset)
                            else -> boundingBox
                        }, type, 1))
                    }
                }
            }
        }

        //Draw main node
        val expansion = ((this.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2
        if (straight) {
            val expX: Double = when (facings.first().axis) {
                EnumFacing.Axis.Z -> expansion
                else -> 0.0
            }
            val expZ: Double = when (facings.first().axis) {
                EnumFacing.Axis.Z -> 0.0
                else -> expansion
            }

            boxes.add(Triple(EnumFacing.NORTH to node.expand(expX * 2, 0.0, expZ * 2)
                    .offset(-expX, 0.0, -expZ)
                    , if (getInstalledTypes().size == 1) getInstalledTypes().first() else null, 0))

        } else {
            boxes.add(Triple(EnumFacing.NORTH to node.expand(expansion * 2, 0.0, expansion * 2).offset(-expansion,
                    0.0,
                    -expansion), if (getInstalledTypes().size == 1) getInstalledTypes().first() else null, 0))
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
            boxes.add(Triple(facing to bb, null, -1))

        }

        return boxes
    }
}
