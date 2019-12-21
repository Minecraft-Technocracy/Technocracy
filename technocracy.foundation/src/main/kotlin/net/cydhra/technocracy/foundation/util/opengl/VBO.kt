package net.cydhra.technocracy.foundation.util.opengl

import org.lwjgl.opengl.GL15
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class VBO {
    var vboId: Int = -1
        private set
    var size: Int = -1
        private set

    var mappedBuffer: ByteBuffer? = null

    constructor(usage: VBOUsage, size: Int) {
        vboId = OpenGLObjectLoader.generateVBO(size, usage.id)
        this.size = size
    }

    constructor(usage: VBOUsage, data: FloatArray) {
        vboId = OpenGLObjectLoader.generateVBO(data, usage.id)
        this.size = data.size
    }

    /**
     * Maps a ByteBuffer to heap regions used by OpenGL
     * used for faster manipulation of the vbo data
     *
     * @param usage the usage of the buffer
     * @return the mapped ByteBuffer
     */
    fun mapBuffer(usage: BufferUsage): ByteBuffer {
        mappedBuffer = OpenGLObjectLoader.getVBOBuffer(vboId, size, usage.id, mappedBuffer)
        return mappedBuffer!!
    }

    /**
     * Manually update the VBO
     * Slow if buffer is big use {@link #mapBuffer(BufferUsage)} instead
     *
     * @param usage the usage of the VBO
     * @param buffer the data that gets written to the vbo
     */
    fun updateVBO(usage: VBOUsage, buffer: FloatBuffer) {
        OpenGLObjectLoader.updateVBO(vboId, buffer, usage.id)
    }

    enum class BufferUsage(val id: Int) {
        READ_ONLY(GL15.GL_READ_ONLY), WRITE_ONLY(GL15.GL_WRITE_ONLY), READ_WRITE(GL15.GL_READ_WRITE)
    }

    enum class VBOUsage(val id: Int) {
        STREAM_DRAW(GL15.GL_STREAM_DRAW), STREAM_READ(GL15.GL_STREAM_READ), STREAM_COPY(GL15.GL_STREAM_COPY),
        STATIC_DRAW(GL15.GL_STATIC_DRAW), STATIC_READ(GL15.GL_STATIC_READ), STATIC_COPY(GL15.GL_STATIC_COPY),
        DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW), DYNAMIC_READ(GL15.GL_DYNAMIC_READ), DYNAMIC_COPY(GL15.GL_DYNAMIC_COPY)
    }
}