package net.cydhra.technocracy.foundation.conduits.parts

import io.netty.buffer.ByteBuf
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.util.EnumFacing

const val NODE_PART_ORDINAL = 0
const val EDGE_PART_ORDINAL = 1
const val ATTACHMENT_PART_ORDINAL = 2

/**
 * A renderable part of the pipe network. It has a defined set of subclasses representing different parts of a pipe
 * node.
 *
 * @see NodePart
 * @see EdgePart
 * @see AttachmentPart
 */
sealed class Part {
    companion object {
        fun readFromByteBuf(buf: ByteBuf): Part {
            val ordinal = buf.readByte()

            return when (ordinal.toInt()) {
                NODE_PART_ORDINAL -> NodePart(buf)
                EDGE_PART_ORDINAL -> EdgePart(buf)
                ATTACHMENT_PART_ORDINAL -> AttachmentPart(buf)
                else -> throw IllegalArgumentException("server sent unknown part ordinal")
            }
        }
    }

    abstract fun writeToByteBuf(buf: ByteBuf)
}

/**
 * A pipe node of a given pipe type. It does not mean the node is attached to anything
 */
data class NodePart(val pipeType: PipeType) : Part() {

    constructor(buf: ByteBuf) : this(PipeType[buf.readByte().toInt()])

    override fun writeToByteBuf(buf: ByteBuf) {
        buf.writeByte(NODE_PART_ORDINAL)
        buf.writeByte(pipeType.ordinal)
    }
}

/**
 * A pipe edge. It connects a node with another node. The other node also has an edge in the direction of the source
 * node. The facing indicates in which direction the other node is.
 */
data class EdgePart(val pipeType: PipeType, val facing: EnumFacing) : Part() {
    constructor(buf: ByteBuf) : this(PipeType[buf.readByte().toInt()], EnumFacing.VALUES[buf.readByte().toInt()])

    override fun writeToByteBuf(buf: ByteBuf) {
        buf.writeByte(EDGE_PART_ORDINAL)
        buf.writeByte(pipeType.ordinal)
        buf.writeByte(facing.ordinal)
    }
}

/**
 * A pipe connection to a machine or similar tile entity.
 */
data class AttachmentPart(val pipeType: PipeType, val facing: EnumFacing) : Part() {

    constructor(buf: ByteBuf) : this(PipeType[buf.readByte().toInt()], EnumFacing.VALUES[buf.readByte().toInt()])

    override fun writeToByteBuf(buf: ByteBuf) {
        buf.writeByte(ATTACHMENT_PART_ORDINAL)
        buf.writeByte(pipeType.ordinal)
        buf.writeByte(facing.ordinal)
    }
}