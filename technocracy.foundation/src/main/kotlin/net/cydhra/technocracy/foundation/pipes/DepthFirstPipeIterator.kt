package net.cydhra.technocracy.foundation.pipes

import org.jgrapht.Graph
import org.jgrapht.traverse.DepthFirstIterator


@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
class DepthFirstPipeIterator : DepthFirstIterator<Network.WrappedBlockPos, Network.WrappedPipeType> {

    /**
     * Creates a new depth-first iterator for the specified graph.
     *
     * @param g the graph to be iterated.
     */
    constructor(g: Graph<Network.WrappedBlockPos, Network.WrappedPipeType>) : this(g, null as Network.WrappedBlockPos)

    /**
     * Creates a new depth-first iterator for the specified graph. Iteration will start at the
     * specified start vertex and will be limited to the connected component that includes that
     * vertex. If the specified start vertex is `null`, iteration will start at an
     * arbitrary vertex and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     */
    constructor(g: Graph<Network.WrappedBlockPos, Network.WrappedPipeType>, startVertex: Network.WrappedBlockPos?) :
            this(g, if (startVertex == null) null else listOf<Network.WrappedBlockPos>(startVertex))

    /**
     * Creates a new depth-first iterator for the specified graph. Iteration will start at the
     * specified start vertices and will be limited to the connected component that includes those
     * vertices. If the specified start vertices is `null`, iteration will start at an
     * arbitrary vertex and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertices the vertices iteration to be started.
     */
    constructor(g: Graph<Network.WrappedBlockPos, Network.WrappedPipeType>, startVertices: Iterable<Network
    .WrappedBlockPos>?) : super(g, if (startVertices == null) null else listOf<Network.WrappedBlockPos>())

    override fun encounterVertex(vertex: Network.WrappedBlockPos, edge: Network.WrappedPipeType) {
        if (vertex.isIONode) {
            putSeenData(vertex, VisitColor.BLACK)
            stack.addLast(vertex)
        } else {
            super.encounterVertex(vertex, edge)
        }
    }
}