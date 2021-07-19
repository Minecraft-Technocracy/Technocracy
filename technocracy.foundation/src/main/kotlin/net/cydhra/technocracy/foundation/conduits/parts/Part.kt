package net.cydhra.technocracy.foundation.conduits.parts

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.util.EnumFacing

/**
 * A renderable part of the pipe network. It has a defined set of subclasses representing different parts of a pipe
 * node.
 *
 * @see NodePart
 * @see EdgePart
 * @see AttachmentPart
 */
sealed class Part

/**
 * A pipe node of a given pipe type. It does not mean the node is attached to anything
 */
data class NodePart(val pipeType: PipeType) : Part()

/**
 * A pipe edge. It connects a node with another node. The other node also has an edge in the direction of the source
 * node. The facing indicates in which direction the other node is.
 */
data class EdgePart(val pipeType: PipeType, val facing: EnumFacing) : Part()

/**
 * A pipe connection to a machine or similar tile entity.
 */
data class AttachmentPart(val pipeType: PipeType, val facing: EnumFacing) : Part()