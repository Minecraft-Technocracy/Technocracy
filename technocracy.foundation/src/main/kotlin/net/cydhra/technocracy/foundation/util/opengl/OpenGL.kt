package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.opengl.GL11

object OpenGLBoundingBox {

    /**
     * Draws a textured axis aligned bounding box using the minecraft tesselator
     */
    fun drawTexturedBoundingBox(boundingBox: AxisAlignedBB, texture: TextureAtlasSprite) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        val minU = texture.minU.toDouble()
        val maxU = texture.maxU.toDouble()
        val minV = texture.minV.toDouble()
        val maxV = texture.maxV.toDouble()

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        //Front Quad
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Back Quad
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(minU, minV).endVertex()
        //Left Quad
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(minU, minV).endVertex()
        //Right Quad
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Top Quad
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(minU, minV).endVertex()
        //Bottom Quad
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxU, maxV).endVertex()
        buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxU, minV).endVertex()
        buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minU, minV).endVertex()
        tessellator.draw()
    }
}