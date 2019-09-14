package net.cydhra.technocracy.foundation.conduits.transit

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.util.INBTSerializable

class TransitSink() : INBTSerializable<NBTTagCompound> {

    companion object {
        private const val NBT_KEY_TYPE = "type"
        private const val NBT_KEY_FACING = "facing"
    }

    lateinit var type: PipeType
        private set

    lateinit var facing: EnumFacing
        private set

    constructor(type: PipeType, facing: EnumFacing) : this() {
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