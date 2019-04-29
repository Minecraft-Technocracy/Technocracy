package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.tileentity.components.NetworkComponent
import net.minecraft.util.EnumFacing
import java.util.*


class TileEntityPipe : AbstractComponentTileEntity() {
    val networkComponent = NetworkComponent()

    init {
        registerComponent(networkComponent, "network")
    }

    fun setNetworkId(uuid: UUID): TileEntityPipe {
        networkComponent.uuid = uuid
        markForUpdate()
        return this
    }

    override fun onLoad() {

        //forge calls onLoad 2x
        if(networkComponent.uuid != null)
            return

        var connected = 0
        for (facing in EnumFacing.values()) {
            val current = pos.offset(facing)
            if (world.getBlockState(current).block is PipeBlock) {
                val pipe = world.getTileEntity(current) as TileEntityPipe
                val uuid = pipe.networkComponent.uuid!!
                if (connected != 0) {
                    //already has connected to a subnet
                    if (uuid != networkComponent.uuid) {
                        //is in different network
                        //combine the two networks
                        Network.combineNetwork(pos, current, uuid, networkComponent.uuid!!, world, Network.PipeType.ENERGY)
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

        if(connected == 0) {
            //no network found, create new one
            Network.addNode(pos, UUID.randomUUID(), world)
        }
    }
}