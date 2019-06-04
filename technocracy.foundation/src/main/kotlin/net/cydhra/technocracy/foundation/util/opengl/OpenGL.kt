package net.cydhra.technocracy.foundation.util.opengl

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11

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