package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.pipes.WrappedBlockPos
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.components.ComponentPipeTypes
import net.cydhra.technocracy.foundation.tileentity.components.NetworkComponent
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import java.util.*


class TileEntityPipe(meta: Int = 0) : AggregatableTileEntity() {
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
                        Network.addEdge(WrappedBlockPos(pos), WrappedBlockPos(current), uuid, world, pipeTypes.types.first())
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
            Network.addNode(WrappedBlockPos(pos),
                    networkComponent.uuid!!,
                    world)
        }

        calculateIOPorts()
    }
}