package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import org.jgrapht.Graph


class FilteredPipeTypeGraph(val graph: Graph<WrappedBlockPos, WrappedPipeType>, val pipeType: PipeType) :Graph<WrappedBlockPos, WrappedPipeType> by graph {
    override fun outgoingEdgesOf(vertex: WrappedBlockPos): Set<WrappedPipeType> {
        return graph.outgoingEdgesOf(vertex).filter { it.pipeType == pipeType }.toSet()
    }
}