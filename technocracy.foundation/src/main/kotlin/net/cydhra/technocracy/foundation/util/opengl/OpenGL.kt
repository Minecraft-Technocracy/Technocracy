package net.cydhra.technocracy.foundation.util.opengl

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fluids.Fluid
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector4f
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import javax.vecmath.Vector2d

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

object ScreenspaceUtil {
    private val projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16)
    private val modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16)
    val viewportMatrixBuffer = GLAllocation.createDirectIntBuffer(16)
    private val positionsBuffer = GLAllocation.createDirectFloatBuffer(4)
    private val invModelViewProjectionMatrtrix = Matrix4f()

    /**
     * Needs to be called once before calling the other methods
     */
    fun initMatrix() {
        this.projectionMatrixBuffer.clear()
        this.modelviewMatrixBuffer.clear()
        this.viewportMatrixBuffer.clear()
        this.positionsBuffer.clear()

        GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, this.modelviewMatrixBuffer)
        GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixBuffer)
        GlStateManager.glGetInteger(GL11.GL_VIEWPORT, this.viewportMatrixBuffer)

        projectionMatrixBuffer.flip().limit(16)
        modelviewMatrixBuffer.flip().limit(16)
        val projMat = Matrix4f().load(projectionMatrixBuffer).invert() as Matrix4f
        val modelMat = Matrix4f().load(modelviewMatrixBuffer).invert() as Matrix4f
        Matrix4f.mul(modelMat, projMat, invModelViewProjectionMatrtrix)
    }

    /**
     * Gets the 3d positions of the mouse position on the near and far plane
     *
     * @param mouseX mouse posX
     * @param mouseY mouse posY
     *
     * @return near plane pos and far plane pos
     */
    fun getPositonsOnFrustum(mouseX: Int, mouseY: Int): List<Vec3d> {
        val near = getPositionFromScreen(mouseX, mouseY, -1f)
        val far = getPositionFromScreen(mouseX, mouseY, 1f)
        return listOf(near, far)
    }

    /**
     * Gets the depth of a pixel from the depth buffer at the corresponding position
     * @param mouseX mouse posX
     * @param mouseY mouse posY
     *
     * @return the depth
     */
    fun getDepthOfPixel(mouseX: Int, mouseY: Int): Float {
        GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, positionsBuffer)
        return positionsBuffer[0]
    }

    /**
     * Gets the world position at the mouse position with the corresponding depth
     * @param mouseX mouse posX
     * @param mouseY mouse posY
     * @param depth the depth of the pixel
     *
     * @return the 3d position
     */
    fun getPositionFromScreen(mouseX: Int, mouseY: Int, depth: Float): Vec3d {
        val posX = viewportMatrixBuffer[0].toDouble()
        val posY = viewportMatrixBuffer[1].toDouble()
        val width = viewportMatrixBuffer[2].toDouble()
        val height = viewportMatrixBuffer[3].toDouble()

        var screenX: Double = (mouseX - posX) / width
        var screenY: Double = (mouseY - posY) / height
        screenX = screenX * 2.0 - 1.0
        screenY = screenY * 2.0 - 1.0

        var tmp = Vector4f()
        tmp.x = screenX.toFloat()
        tmp.y = screenY.toFloat()
        tmp.z = depth
        tmp.w = 1.0f
        tmp = Matrix4f.transform(invModelViewProjectionMatrtrix, tmp, null)

        val w = tmp.w.toDouble()
        return Vec3d(tmp.x / w, tmp.y / w, tmp.z / w)
    }
}

object OpenGLObjectLoader {
    fun generateVBO(data: FloatArray, usage: Int): Int {
        val vboID = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        //put data into buffer
        val buffer = GLAllocation.createDirectFloatBuffer(data.size)
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

    fun addFloatAttributeToVBO(vbo: Int, attributeIndex: Int, dataSize: Int, stride: Int = 0, offset: Int = 0) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL20.glVertexAttribPointer(attributeIndex, dataSize, GL11.GL_FLOAT, false, stride * 4, offset * 4L)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
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
    fun drawTexturedBoundingBox(boundingBox: AxisAlignedBB, minUV: Vector2d = Vector2d(0.0, 0.0), maxUV: Vector2d = Vector2d(1.0, 1.0), color: Int = -1) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        val minU = minUV.x
        val maxU = maxUV.x
        val minV = minUV.y
        val maxV = maxUV.y

        if(color != -1) {
            val f3 = (color shr 24 and 255).toFloat() / 255.0f
            val f = (color shr 16 and 255).toFloat() / 255.0f
            val f1 = (color shr 8 and 255).toFloat() / 255.0f
            val f2 = (color and 255).toFloat() / 255.0f
            GlStateManager.color(f, f1, f2, f3)
        }

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

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
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