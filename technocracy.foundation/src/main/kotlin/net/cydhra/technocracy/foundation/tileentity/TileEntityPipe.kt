package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.blocks.general.pipe
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.pipes.WrappedBlockPos
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.components.ComponentPipeTypes
import net.cydhra.technocracy.foundation.tileentity.components.NetworkComponent
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import java.util.*


class TileEntityPipe(meta: Int = 0) : AggregatableTileEntity() {
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

    init {
        registerComponent(networkComponent, "network")
        registerComponent(pipeTypes, "pipeTypes")

        pipeTypes.types.add(PipeType.values()[meta])
    }

    fun hasPipeType(type: PipeType): Boolean {
        return pipeTypes.types.contains(type)
    }

    fun addPipeType(type: PipeType) {
        pipeTypes.types.add(type)
        //todo update network pipe tier
        markForUpdate()
    }

    fun getInstalledTypes(): List<PipeType> {
        return listOf(*pipeTypes.types.toTypedArray())
    }

    fun getNetworkId(): UUID {
        return networkComponent.uuid!!
    }

    fun setNetworkId(uuid: UUID): TileEntityPipe {
        networkComponent.uuid = uuid
        markForUpdate()
        return this
    }

    fun rotateIO() {
        Network.rotateIO(pos, networkComponent.uuid!!, world)
    }

    fun calculateIOPorts() {
        Network.removeIOFromNode(pos, networkComponent.uuid!!, world)

        for (facing in EnumFacing.values()) {
            val current = pos.offset(facing)
            val tile = world.getTileEntity(current)
            if (tile != null && tile !is TileEntityPipe) {
                val pipe = pipeTypes.types.first()
                if (tile.hasCapability(pipe.capability!!, facing.opposite)) {
                    Network.addIOToNode(pos, facing, networkComponent.uuid!!, world, pipe)
                }
            }
        }
    }

    override fun onLoad() {
        //forge calls onLoad 2x (Client/Server)
        if (networkComponent.uuid != null || world.isRemote) return

        var connected = 0
        for (facing in EnumFacing.values()) {
            val current = pos.offset(facing)
            if (world.getBlockState(current).block is PipeBlock) {
                val pipe = world.getTileEntity(current) as TileEntityPipe
                if (pipe.networkComponent.uuid == null) {
                    TCFoundation.logger.error("No networkId found")
                }
                val uuid = pipe.networkComponent.uuid!!
                if (connected != 0) {
                    //already has connected to a subnet
                    if (uuid != networkComponent.uuid) {
                        //is in different network
                        //combine the two networks
                        Network.combineNetwork(WrappedBlockPos(pos),
                                WrappedBlockPos(current),
                                uuid,
                                networkComponent.uuid!!,
                                world,
                                pipeTypes.types.first())
                    } else {
                        //is same network add an edge
                        Network.addEdge(WrappedBlockPos(pos),
                                WrappedBlockPos(current),
                                uuid,
                                world,
                                pipeTypes.types.first())
                    }
                } else {
                    setNetworkId(uuid)
                    Network.addEdge(WrappedBlockPos(pos),
                            WrappedBlockPos(current),
                            uuid,
                            world,
                            pipeTypes.types.first())
                }

                connected++
            }
        }

        if (connected == 0) {
            //no network found, create new one
            setNetworkId(UUID.randomUUID())
            //TODO current pipe tier
            Network.addNode(WrappedBlockPos(pos), networkComponent.uuid!!, world)
        }

        calculateIOPorts()
    }

    /**
     * Returns a list of triples which contain information about the pipe model parts
     * First is the boundingbox
     * Second is the PipeType
     * Third is an integer indicating which model part type it is (0: node, 1: connection)
     */
    fun getPipeModelParts(): List<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType, Int>> {
        val boxes = mutableListOf<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType, Int>>()

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

                        //Add node
                        if (straight) {
                            //Special case if 2 pipes merge into one
                            val canRenderNode =
                                    neighbourPipe.getInstalledTypes().size < this.getInstalledTypes().size && nodeConnectionOffset == 0.0

                            if (!canRenderNode) {
                                boxes.add(Triple(facing to when {
                                    facing.axis == EnumFacing.Axis.X -> node.offset(0.0, 0.0, nodeConnectionOffset)
                                    facing.axis == EnumFacing.Axis.Z -> node.offset(-nodeConnectionOffset, 0.0, 0.0)
                                    facing.axis.isVertical -> node.offset(0.0, 0.0, nodeConnectionOffset)
                                    else -> node
                                }, type, 0))
                            }
                        }
                    }
                }
            }

            //Draw main node
            if (!straight) {
                val expansion = ((this.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2
                boxes.add(Triple(EnumFacing.NORTH to node.expand(expansion * 2, 0.0, expansion * 2).offset(-expansion,
                        0.0,
                        -expansion),
                        type,
                        0))
            }
        }

        return boxes
    }
}