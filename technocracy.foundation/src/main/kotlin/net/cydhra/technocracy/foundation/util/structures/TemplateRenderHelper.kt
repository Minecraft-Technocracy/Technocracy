package net.cydhra.technocracy.foundation.util.structures

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.crash.CrashReport
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.ReportedException
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

object TemplateRenderHelper {

    /**
     * @param bufferIn the vertex buffer that will be rendered
     * @param posX the world x position
     * @param posY the world x position
     * @param posZ the world x position
     */
    fun renderVBO(bufferIn: VertexBuffer?, posX: Double, posY: Double, posZ: Double) {
        if (bufferIn == null) return

        GL11.glPushMatrix()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        RenderHelper.disableStandardItemLighting()

        val mc = Minecraft.getMinecraft()

        GlStateManager.disableLighting()
        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_MODULATE)
        GL11.glColor4f(1f,1f,1f,0.2f)


        val intX = posX - mc.renderManager.viewerPosX
        val intY = posY - mc.renderManager.viewerPosY
        val intZ = posZ - mc.renderManager.viewerPosZ

        GL11.glTranslated(intX - 0.5, intY, intZ - 0.5)

        mc.entityRenderer.enableLightmap()

        if (Minecraft.isAmbientOcclusionEnabled())
            GlStateManager.shadeModel(7425)

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY)

        bufferIn.bindBuffer()
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L)
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L)
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit)
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        bufferIn.drawArrays(7)

        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0)
        GlStateManager.resetColor()

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY)
        GlStateManager.resetColor()

        mc.entityRenderer.disableLightmap()

        GlStateManager.enableLighting()
        RenderHelper.enableStandardItemLighting()

        GL11.glPopMatrix()
    }

    fun generateVBO(template: Template, bufferIn: VertexBuffer?, position: BlockPos, rerender: Boolean = false): VertexBuffer {
        var buffer = bufferIn;
        if (buffer != null) {
            if (!rerender)
                return buffer
            buffer.deleteGlBuffers()
        }

        val mc = Minecraft.getMinecraft()

        buffer = VertexBuffer(DefaultVertexFormats.BLOCK)

        val tess = Tessellator.getInstance()
        val bufferBuilder = tess.buffer
        bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)


        mc.entityRenderer.enableLightmap()

        //calc the dimensions of the template
        var minX = 0
        var minY = 0
        var minZ = 0
        var maxX = 0
        var maxY = 0
        var maxZ = 0
        for (info in template.blocks) {
            minX = min(info.pos.x, minX)
            maxX = max(info.pos.x, maxX)
            minY = min(info.pos.y, minY)
            maxY = max(info.pos.y, maxY)
            minZ = min(info.pos.z, minZ)
            maxZ = max(info.pos.z, maxZ)
        }

        val wrappedWorld = TemplateClientWorld(mc.world, template.blocks, position)

        for (info in template.blocks) {

            var state = wrappedWorld.getBlockState(info.pos).getActualState(wrappedWorld, info.pos)

            try {
                val renderType = state.renderType
                if (renderType == EnumBlockRenderType.INVISIBLE) {
                    continue
                } else {
                    when (renderType) {
                        EnumBlockRenderType.MODEL -> {
                            val model = mc.blockRendererDispatcher.getModelForState(state)
                            state = state.block.getExtendedState(state, wrappedWorld, info.pos)
                            mc.blockRendererDispatcher.blockModelRenderer.renderModel(wrappedWorld, model, state, info.pos, bufferBuilder, true, MathHelper.getPositionRandom(info.pos))
                        }
                        else -> {
                        }
                        //Todo add fluids?
                    }
                }
            } catch (throwable: Throwable) {
                val crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world")
                throw ReportedException(crashreport)
            }
        }

        bufferBuilder.finishDrawing()
        bufferBuilder.reset()
        buffer.bufferData(bufferBuilder.byteBuffer)
        return buffer
    }
}