package net.cydhra.technocracy.foundation.conduits.nodes

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.util.math.BlockPos

/**
 * A conduit network edge. It connects two nodes identified by their positions and the type of the edge
 *
 */
internal class ConduitEdge(val posNodeA: BlockPos, val posNodeB: BlockPos, val type: PipeType)