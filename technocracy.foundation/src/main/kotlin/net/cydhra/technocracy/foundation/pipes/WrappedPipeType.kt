package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import org.jgrapht.graph.DefaultEdge
import java.util.*


data class WrappedPipeType(val pipeType: PipeType) : DefaultEdge() {
    val uuid: UUID? = UUID.randomUUID()
    fun getSourceNode(): WrappedBlockPos {
        return super.getSource() as WrappedBlockPos
    }

    fun getTargetNode(): WrappedBlockPos {
        return super.getTarget() as WrappedBlockPos
    }

    override fun equals(other: Any?): Boolean {
        return other is WrappedPipeType && other.uuid?.equals(uuid)!!
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}