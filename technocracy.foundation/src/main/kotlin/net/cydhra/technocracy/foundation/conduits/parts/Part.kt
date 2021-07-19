package net.cydhra.technocracy.foundation.conduits.parts

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.util.EnumFacing

/**
 * A renderable part of the pipe network
 */
sealed class Part

data class NodePart(val pipeType: PipeType) : Part()

data class EdgePart(val pipeType: PipeType, val facing: EnumFacing) : Part()

data class AttachmentPart(val pipeType: PipeType, val facing: EnumFacing) : Part()