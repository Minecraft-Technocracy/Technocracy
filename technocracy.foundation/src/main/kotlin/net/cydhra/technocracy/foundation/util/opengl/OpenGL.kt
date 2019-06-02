package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.opengl.GL11

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


}

object Rect {
    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    fun drawModalRectWithCustomSizedTexture(x: Int, y: Int, u: Float, v: Float, width: Float, height: Float, textureWidth: Float, textureHeight: Float) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(x.toDouble(), (y + height).toDouble(), 0.0).tex((u * f).toDouble(), ((v + height) * f1).toDouble()).endVertex()
        bufferbuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(((u + width) * f).toDouble(), ((v + height) * f1).toDouble()).endVertex()
        bufferbuilder.pos((x + width).toDouble(), y.toDouble(), 0.0).tex(((u + width) * f).toDouble(), (v * f1).toDouble()).endVertex()
        bufferbuilder.pos(x.toDouble(), y.toDouble(), 0.0).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
        tessellator.draw()
    }
}