package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.INBTSerializable

/**
 * A specialized kind of transit edge, that represents a connection between the conduit network and a machine or
 * storage interface.
 */
class TransitSink(val pos: BlockPos) : TransitEdge() {
    constructor(type: PipeType, facing: EnumFacing, pos: BlockPos) : this(pos) {
        this.type = type
        this.facing = facing
    }
}