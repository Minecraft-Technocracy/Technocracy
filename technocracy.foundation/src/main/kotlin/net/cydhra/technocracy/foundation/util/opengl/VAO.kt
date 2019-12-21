package net.cydhra.technocracy.foundation.util.opengl

import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30


class VAO {

    /**
     * The internal id of the vao
     */
    val vaoId: Int = GL30.glGenVertexArrays()

    /**
     * the current amount of attributes in this vao
     */
    private var currentAttributeIndex: Int = 0

    /**
     * the current offset in the vbo
     */
    private var currentVBOOffset: Int = 0
    private var currentVBO: VBO? = null

    /**
     * Link a vbo to use for attribute binding
     *
     * @param vbo the VBO to bind
     * @return this VAO
     */
    fun linkVBO(vbo: VBO): VAO {
        currentVBO = vbo
        currentVBOOffset = 0
        return this
    }

    /**
     * Add a attribute the vao of the given size
     *
     * @param size the amount of floats for this attribute
     * @return this VAO
     */
    fun addFloatAttribute(size: Int): VAO {
        if (currentVBO != null) {
            OpenGLObjectLoader.addFloatAttributeToVAO(vaoId, currentVBO!!.vboId, currentAttributeIndex++, size)
            currentVBOOffset += size
        }
        return this
    }

    /**
     * Bind an attribute with specific size to the VAO
     *
     * @param attributeSize the amount of floats for this attribute
     * @param instanceSize the size of the whole element per instance
     * @return this VAO
     */
    fun addInstancedFloatAttribute(attributeSize: Int, instanceSize: Int): VAO {
        return addInstancedFloatAttribute(attributeSize, instanceSize, currentVBOOffset)
    }

    /**
     * Bind an attribute with specific size to the VAO
     *
     * @param attributeSize the amount of floats for this attribute
     * @param instanceSize the size of the whole element per instance
     * @param offset the offset of the data in the VBO
     * @return this VAO
     */
    fun addInstancedFloatAttribute(attributeSize: Int, instanceSize: Int, offset: Int): VAO {
        if (currentVBO != null) {
            OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vaoId, currentVBO!!.vboId, currentAttributeIndex++, attributeSize, instanceSize, currentVBOOffset)
            currentVBOOffset = offset + attributeSize
        }
        return this
    }

    /**
     * Bind the VAO and enable the attributes
     */
    fun bindVAO() {
        GL30.glBindVertexArray(vaoId)
        //enable attributes
        for (i in 0 until currentAttributeIndex) {
            GL20.glEnableVertexAttribArray(i)
        }
    }

    /**
     * Unbind the VAO and disable the attributes
     */
    fun unbindVAO() {
        //disable attributes
        for (i in 0 until currentAttributeIndex) {
            GL20.glDisableVertexAttribArray(i)
        }
        GL30.glBindVertexArray(0)
    }

}