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
 * @param id a chunk-unique id of this edge. Used for connections between edges
 *
 * @see [TransitSink]
 */
abstract class TransitEdge(val pos: BlockPos) : INBTSerializable<NBTTagCompound> {

    companion object {
        private const val NBT_KEY_ID = "id"
        private const val NBT_KEY_TYPE = "type"
        private const val NBT_KEY_FACING = "facing"
        private const val NBT_KEY_PATHS = "paths"
    }

    var id: Int = -1
        protected set

    lateinit var type: PipeType
        protected set

    lateinit var facing: EnumFacing
        protected set

    val paths: MutableMap<Int, Int> = mutableMapOf()

    operator fun component1(): PipeType = type

    operator fun component2(): EnumFacing = facing

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.id = nbt.getInteger(NBT_KEY_ID)
        this.type = PipeType.values()[nbt.getInteger(NBT_KEY_TYPE)]
        this.facing = EnumFacing.values()[nbt.getInteger(NBT_KEY_FACING)]

        val pathTag = nbt.getCompoundTag(NBT_KEY_PATHS)

        for (key in pathTag.keySet) {
            this.paths[key.toInt()] = pathTag.getInteger(key)
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setInteger(NBT_KEY_ID, this@TransitEdge.id)
            setInteger(NBT_KEY_TYPE, this@TransitEdge.type.ordinal)
            setInteger(NBT_KEY_FACING, this@TransitEdge.facing.ordinal)

            val paths = NBTTagCompound()
            this@TransitEdge.paths.forEach { (id, cost) ->
                paths.setInteger(id.toString(), cost)
            }
            setTag(NBT_KEY_PATHS, paths)
        }
    }
}