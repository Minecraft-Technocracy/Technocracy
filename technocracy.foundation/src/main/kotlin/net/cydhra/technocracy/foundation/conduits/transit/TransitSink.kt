package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * A specialized kind of transit edge, that represents a connection between the conduit network and a machine or
 * storage interface.
 */
class TransitSink(pos: BlockPos) : TransitEdge(pos) {
    constructor(id: Int, type: PipeType, facing: EnumFacing, pos: BlockPos) : this(pos) {
        this.id = id
        this.type = type
        this.facing = facing
    }
}