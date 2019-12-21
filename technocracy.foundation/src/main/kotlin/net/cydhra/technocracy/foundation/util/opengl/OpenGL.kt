package net.cydhra.technocracy.foundation.util.opengl

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fluids.Fluid
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer

object OpenGLFluidRenderer {

    enum class FluidState {
        STILL, FLOWING
    }

    /**
     * Draws a bounding box with a fluid texture
     */
    fun drawFluidBoundingBox(boundingBox: AxisAlignedBB, fluid: Fluid, state: FluidState) {
        val texture =
                TextureAtlasManager.getTextureAtlasSprite(if (state == FluidState.STILL) fluid.still else fluid.flowing)
        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        OpenGLBoundingBox.drawTexturedBoundingBox(boundingBox, texture)
    }
}

object OpenGLObjectLoader {
    fun generateVBO(data: FloatArray, usage: Int): Int {
        val vboID = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        //put data into buffer
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        //put buffer into vbo
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage)
        //unbind vbo
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return vboID
    }

    fun generateVBO(size: Int, usage: Int): Int {
        val vboID = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        //set vbo size
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size.toLong(), usage)
        //unbind vbo
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return vboID
    }

    fun getVBOBuffer(vbo: Int, size: Int, usage: Int): ByteBuffer {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        val buffer = GL15.glMapBuffer(GL15.GL_ARRAY_BUFFER, usage, size.toLong(), null)
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return buffer
    }

    fun getVBOBuffer(vbo: Int, size: Int, usage: Int, bufferIn: ByteBuffer?): ByteBuffer {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        val buffer = GL15.glMapBuffer(GL15.GL_ARRAY_BUFFER, usage, size.toLong(), bufferIn)
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return buffer
    }

    fun addInstancedFloatAttributeToVAO(vao: Int, vbo: Int, attributeIndex: Int, dataSize: Int, stride: Int, offset: Int) {
        GL30.glBindVertexArray(vao)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL20.glVertexAttribPointer(attributeIndex, dataSize, GL11.GL_FLOAT, false, stride * 4, offset * 4L)
        GL33.glVertexAttribDivisor(attributeIndex, 1)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    fun addFloatAttributeToVAO(vao: Int, vbo: Int, attributeIndex: Int, dataSize: Int, stride: Int = 0, offset: Int = 0) {
        GL30.glBindVertexArray(vao)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL20.glVertexAttribPointer(attributeIndex, dataSize, GL11.GL_FLOAT, false, stride * 4, offset * 4L)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    fun updateVBO(vbo: Int, buffer: FloatBuffer, usage: Int) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        //assign new buffer, faster then cleaning the data
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity().toLong(), usage)
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }
}

object OpenGLBoundingBox {

    /**
     * Draws a textured axis aligned bounding box using the minecraft tesselator
     */
    fun drawTexturedBoundingBox(boundingBox: AxisAlignedBB) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        val minU = 0.0
        val maxU = 1.0
        val minV = 0.0
        val maxV = 1.0

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        //Front Quad (NORTH)
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(minU, minV).endVertex()
        //Back Quad (SOUTH)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Left Quad (WEST)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(minU, minV).endVertex()
        //Right Quad (EAST)
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Top Quad (UP)
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Bottom Quad (DOWN)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minU, minV).endVertex()
        tessellator.draw()
    }

    fun drawTexturedBoundingBox(boundingBox: AxisAlignedBB, texture: TextureAtlasSprite) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        val minU = texture.minU.toDouble()
        val maxU = texture.maxU.toDouble()
        val minV = texture.minV.toDouble()
        val maxV = texture.maxV.toDouble()

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        //Front Quad (NORTH)
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(minU, minV).endVertex()
        //Back Quad (SOUTH)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Left Quad (WEST)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(minU, minV).endVertex()
        //Right Quad (EAST)
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Top Quad (UP)
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Bottom Quad (DOWN)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minU, minV).endVertex()
        tessellator.draw()
    }


    /**
     * Draws an axis aligned bounding box using the minecraft tesselator
     */
    fun drawBoundingBox(boundingBox: AxisAlignedBB) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        //Front Quad (NORTH)
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        //Back Quad (SOUTH)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        //Left Quad (WEST)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        //Right Quad (EAST)
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        //Top Quad (UP)
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        //Bottom Quad (DOWN)
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        tessellator.draw()
    }
}