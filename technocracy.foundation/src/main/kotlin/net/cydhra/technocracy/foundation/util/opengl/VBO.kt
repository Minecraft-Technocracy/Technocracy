package net.cydhra.technocracy.foundation.util.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class VBO {
    var vboId: Int = -1
        private set
    var size: Int = -1
        private set

    var mappedBuffer: ByteBuffer? = null

    /**
     * the current amount of attributes in this vbo
     */
    private var currentAttributeIndex: Int = 0

    /**
     * the current offset in the vbo
     */
    private var currentVBOOffset: Int = 0

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
     * Add a attribute the vao of the given size
     *
     * @param size the amount of floats for this attribute
     * @param stride the maximum size of all attributes that are going to be added
     * @return this VBO
     */
    fun addFloatAttribute(size: Int, stride: Int = size, attributeIndex: Int = currentAttributeIndex++): VBO {
        OpenGLObjectLoader.addFloatAttributeToVBO(vboId, attributeIndex, size, stride, currentVBOOffset)
        currentVBOOffset += size
        return this
    }

    /**
     * Used for a single draw call
     *
     * @param mode the gl mode
     * @param first the index of the first byte to read from
     * @param amount the amount of objects to draw
     */
    fun drawSingle(mode: Int, first: Int = 0, amount: Int = size) {
        bindVBO()
        GL11.glDrawArrays(mode, first, amount)
        unbindVBO()
    }

    /**
     * Bind the VBO and enable the attributes
     */
    fun bindVBO() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        //enable attributes
        for (i in 0 until currentAttributeIndex) {
            GL20.glEnableVertexAttribArray(i)
        }
    }

    /**
     * Unbind the VAO and disable the attributes
     */
    fun unbindVBO() {
        //disable attributes
        for (i in 0 until currentAttributeIndex) {
            GL20.glDisableVertexAttribArray(i)
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
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