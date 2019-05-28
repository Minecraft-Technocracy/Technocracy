package net.cydhra.technocracy.foundation.client.renderer.tileEntity

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.GL_MODELVIEW
import org.lwjgl.opengl.GL11.GL_TEXTURE


class PipeRenderer : TileEntitySpecialRenderer<TileEntityPipe>() {

    override fun render(te: TileEntityPipe, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int,
            alpha: Float) {

        GlStateManager.pushMatrix()

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage])
            GlStateManager.matrixMode(GL_TEXTURE)
            GlStateManager.pushMatrix()
            GlStateManager.scale(4.0F, 4.0F, 1.0F)
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F)
            GlStateManager.matrixMode(GL_MODELVIEW)
        }

        GlStateManager.enableRescaleNormal()

        if (destroyStage < 0) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, alpha)
        }

        //Translate already interpolated positions
        GlStateManager.translate(x, y, z)

        te.getPipeModelParts().forEach {
            when (it.third) {
                0 -> this.bindTexture(this.getTextureForNodeType(it.second))
                1 -> this.bindTexture(this.getTextureForConnectionType(it.second))
            }

            OpenGLBoundingBox.drawTexturedBoundingBox(it.first)
        }

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL_TEXTURE)
            GlStateManager.popMatrix()
            GlStateManager.matrixMode(GL_MODELVIEW)
        }

        GlStateManager.popMatrix()
    }

    /**
     * Returns the texture for connections of a specific pipe type
     */
    private fun getTextureForConnectionType(type: PipeType): ResourceLocation {
        return when (type) {
            PipeType.ENERGY -> TextureAtlasManager.pipe_energy
            PipeType.FLUID -> TextureAtlasManager.pipe_fluid
            PipeType.ITEM -> TextureAtlasManager.pipe_item
        }
    }

    /**
     * Returns the texture for nodes of a specific pipe type
     */
    private fun getTextureForNodeType(type: PipeType): ResourceLocation {
        return TextureAtlasManager.pipe_node
    }
}