package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.tileentity.components.ComponentPipeTypes
import net.cydhra.technocracy.foundation.tileentity.components.NetworkComponent
import net.minecraft.util.EnumFacing
import java.util.*


class TileEntityPipe(val meta: Int = 0) : AggregatableTileEntity() {
    private val networkComponent = NetworkComponent()
    private val pipeTypes = ComponentPipeTypes()

    init {
        registerComponent(networkComponent, "network")
        registerComponent(pipeTypes, "pipeTypes")

        pipeTypes.types.add(Network.PipeType.values()[meta])
    }

    fun hasPipeType(type: Network.PipeType): Boolean {
        return pipeTypes.types.contains(type)
    }

    fun addPipeType(type: Network.PipeType) {
        pipeTypes.types.add(type)
        markForUpdate()
    }

    fun getInstalledTypes(): List<Network.PipeType> {
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

    override fun onLoad() {
        //forge calls onLoad 2x
        if (networkComponent.uuid != null || world.isRemote)
            return

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
                        Network.combineNetwork(Network.WrappedBlockPos(pos), Network.WrappedBlockPos(pos), uuid, networkComponent.uuid!!, world,
                                Network.PipeType
                                .ENERGY)
                    } else {
                        //is same network add an edge
                        Network.addEdge(pos, current, uuid, world, Network.PipeType.ENERGY)
                    }
                } else {
                    setNetworkId(uuid)
                    Network.addEdge(pos, current, uuid, world, Network.PipeType.ENERGY)
                }

                connected++
            }
        }

        if (connected == 0) {
            //no network found, create new one
            setNetworkId(UUID.randomUUID())
            Network.addNode(Network.WrappedBlockPos(pos), networkComponent.uuid!!, world)
        }
    }
}