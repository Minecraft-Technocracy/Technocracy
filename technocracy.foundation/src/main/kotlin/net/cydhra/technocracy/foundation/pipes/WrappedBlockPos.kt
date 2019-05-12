package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos


data class WrappedBlockPos(val pos: BlockPos) {
    val io = mutableMapOf<PipeType, MutableMap<Network.IO, MutableSet<EnumFacing>>>()
    //TODO priority pipes

    val hasIO: Boolean
        get() {
            return io.isNotEmpty() && io.filter { !it.value.isNullOrEmpty() }.isNotEmpty()
        }

    fun getInputFacings(type: PipeType, strictInput: Boolean): Set<EnumFacing> {
        val map = io[type]
        if (map != null) {
            val combined = mutableSetOf<EnumFacing>()
            map.filter { if (strictInput) it.key == Network.IO.INPUT else it.key != Network.IO.OUTPUT }.forEach { ioEnum, facing ->
                combined.addAll(facing)
            }
            return combined
        }
        return emptySet()
    }

    fun getOutputFacings(type: PipeType, strictOutput: Boolean): Set<EnumFacing> {
        val map = io[type]
        if (map != null) {
            val combined = mutableSetOf<EnumFacing>()
            map.filter { if (strictOutput) it.key == Network.IO.OUTPUT else it.key != Network.IO.INPUT }.forEach { ioEnum, facing ->
                combined.addAll(facing)
            }
            return combined
        }
        return emptySet()
    }

    override fun equals(other: Any?): Boolean {
        if (other is WrappedBlockPos) {
            return other.pos == pos
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }
}