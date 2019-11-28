package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer

/**
 * A specialized kind of transit edge, that represents a connection between the conduit network and a machine or
 * storage interface.
 */
class TransitSink(pos: BlockPos) : TransitEdge(pos) {

    private var transferCoolDown = 0

    // TODO: this is not assigned yet
    lateinit var routingStrategy: RoutingStrategy

    constructor(id: Int, type: PipeType, facing: EnumFacing, pos: BlockPos) : this(pos) {
        this.id = id
        this.type = type
        this.facing = facing
    }

    fun offersContent(world: WorldServer): Boolean {
        if (transferCoolDown > 0) {
            return false
        }

        return false
    }

    fun acceptsContent(world: WorldServer, content: Any): Boolean {
        if (transferCoolDown > 0) {
            return false
        }

        return false
    }

    /**
     * Tick this sink. This will just tick down the transfer cool-down
     */
    fun tick() {
        if (transferCoolDown > 0)
            transferCoolDown--
    }
}
