package net.cydhra.technocracy.foundation.conduits.nodes

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.util.math.BlockPos

/**
 * A node within the conduit network.
 */
internal class ConduitNode(val pos: BlockPos, val type: PipeType)