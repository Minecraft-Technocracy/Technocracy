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