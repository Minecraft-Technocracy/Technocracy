package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.INBTSerializable

/**
 * A edge in the transit-network that is a temporary compression of the conduit network. The transit network It is used
 * to efficiently traverse the conduit network, skipping nodes that do not do anything within the network except
 * connecting other nodes. It is also used to efficiently insert and remove chunks from the conduit network while
 * maintaining sound functionality.
 * Note, that there are no transit nodes, as the only relevant part that is modelled by the transit network are
 * connections between chunks and connections between the network and its sinks.
 *
 * @see [TransitSink]
 */
abstract class TransitEdge : INBTSerializable<NBTTagCompound> {

    companion object {
        private const val NBT_KEY_TYPE = "type"
        private const val NBT_KEY_FACING = "facing"
    }

    lateinit var type: PipeType
        protected set

    lateinit var facing: EnumFacing
        protected set

    operator fun component1(): PipeType = type

    operator fun component2(): EnumFacing = facing

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.type = PipeType.values()[nbt.getInteger(NBT_KEY_TYPE)]
        this.facing = EnumFacing.values()[nbt.getInteger(NBT_KEY_FACING)]
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setInteger(NBT_KEY_TYPE, this@TransitEdge.type.ordinal)
            setInteger(NBT_KEY_FACING, this@TransitEdge.facing.ordinal)
        }
    }
}