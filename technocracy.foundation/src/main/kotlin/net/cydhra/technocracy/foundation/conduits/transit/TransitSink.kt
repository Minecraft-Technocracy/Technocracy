package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.common.util.INBTSerializable

/**
 * A data model for tile entities interacting with the conduit network. The sink is located at the pipe that connects
 * to the respective tile entity. It is used during routing
 *
 * @param chunkPos the chunk position of the chunk where this sink is located. This entity does not hold a reference
 * to the chunk to prevent memory leaks (on long-term-cached routes)
 */
class TransitSink(val chunkPos: ChunkPos) : INBTSerializable<NBTTagCompound> {

    companion object {
        private const val NBT_KEY_TYPE = "type"
        private const val NBT_KEY_FACING = "facing"
    }

    lateinit var type: PipeType
        private set

    lateinit var facing: EnumFacing
        private set

    constructor(chunkPos: ChunkPos, type: PipeType, facing: EnumFacing) : this(chunkPos) {
        this.type = type
        this.facing = facing
    }

    operator fun component1(): PipeType = type

    operator fun component2(): EnumFacing = facing

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.type = PipeType.values()[nbt.getInteger(NBT_KEY_TYPE)]
        this.facing = EnumFacing.values()[nbt.getInteger(NBT_KEY_FACING)]
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setInteger(NBT_KEY_TYPE, this@TransitSink.type.ordinal)
            setInteger(NBT_KEY_FACING, this@TransitSink.facing.ordinal)
        }
    }

}