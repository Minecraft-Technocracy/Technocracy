package net.cydhra.technocracy.astronautics.client.render.entity

import net.cydhra.technocracy.astronautics.entity.EntityRocket
import net.cydhra.technocracy.astronautics.util.WrappedClientWorld
import net.cydhra.technocracy.foundation.util.WrappedState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.crash.CrashReport
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.ReportedException
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min


class RenderEntityRocket(renderManagerIn: RenderManager) : Render<EntityRocket>(renderManagerIn) {

    override fun doRender(entity: EntityRocket, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {

        /*for(info in entity.template.blocks) {

            val iblockstate = info.block.getStateFromMeta(info.meta)

            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GlStateManager.pushMatrix()
            GlStateManager.disableLighting()
            val tessellator = Tessellator.getInstance()
            val bufferbuilder = tessellator.buffer

            if (this.renderOutlines) {
                GlStateManager.enableColorMaterial()
                GlStateManager.enableOutlineMode(this.getTeamColor(entity))
            }

            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK)
            val blockpos = BlockPos(entity.posX, entity.entityBoundingBox.maxY, entity.posZ)
            GlStateManager.translate((x - blockpos.getX().toDouble() - 0.5 + info.pos.x).toFloat(), (y - blockpos.getY().toDouble() + info.pos.y).toFloat(), (z - blockpos.getZ().toDouble() - 0.5 + info.pos.z).toFloat())
            val blockrendererdispatcher = Minecraft.getMinecraft().blockRendererDispatcher
            blockrendererdispatcher.blockModelRenderer.renderModel(entity.world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos, bufferbuilder, false)
            tessellator.draw()

            if (this.renderOutlines) {
                GlStateManager.disableOutlineMode()
                GlStateManager.disableColorMaterial()
            }

            GlStateManager.enableLighting()
            GlStateManager.popMatrix()
        }*/


        GL11.glPushMatrix()

        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        RenderHelper.disableStandardItemLighting()


        generateVBO(entity)

        val mc = Minecraft.getMinecraft()



        GlStateManager.disableLighting()
        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        val intX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
        val intY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
        val intZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ

        GL11.glTranslated(intX - 0.5, intY, intZ - 0.5)
        //GL11.glTranslated(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ)

        mc.entityRenderer.enableLightmap()

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY)

        entity.vbo!!.bindBuffer()
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L)
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L)
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit)
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        entity.vbo!!.drawArrays(7)

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

    fun generateVBO(rocket: EntityRocket) {
        if (rocket.vbo != null) return

        rocket.vbo = VertexBuffer(DefaultVertexFormats.BLOCK)

        val tess = Tessellator.getInstance()
        val worldrenderer = tess.buffer

        worldrenderer.begin(7, DefaultVertexFormats.BLOCK)

        val mc = Minecraft.getMinecraft()

        mc.entityRenderer.enableLightmap()

        var minX = 0
        var minY = 0
        var minZ = 0
        var maxX = 0
        var maxY = 0
        var maxZ = 0
        for (info in rocket.template.blocks) {
            minX = min(info.pos.x, minX)
            maxX = max(info.pos.x, maxX)
            minY = min(info.pos.y, minY)
            maxY = max(info.pos.y, maxY)
            minZ = min(info.pos.z, minZ)
            maxZ = max(info.pos.z, maxZ)
        }

        val wrappedWorld = WrappedClientWorld(mc.world, rocket.template.blocks, rocket.position)

        for (info in rocket.template.blocks) {

            var state = wrappedWorld.getBlockState(info.pos).getActualState(wrappedWorld, info.pos)

            try {
                val enumblockrendertype = state.renderType

                if (enumblockrendertype == EnumBlockRenderType.INVISIBLE) {
                    continue
                } else {
                    when (enumblockrendertype) {
                        EnumBlockRenderType.MODEL -> {
                            val model = mc.blockRendererDispatcher.getModelForState(state)
                            state = state.block.getExtendedState(state, wrappedWorld, info.pos)

                            mc.blockRendererDispatcher.blockModelRenderer.renderModel(wrappedWorld, model, state, info.pos, worldrenderer, true, MathHelper.getPositionRandom(info.pos))
                        }
                        else -> {
                        }
                    }
                }
            } catch (throwable: Throwable) {
                val crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world")
                throw ReportedException(crashreport)
            }
        }

        worldrenderer.finishDrawing()
        worldrenderer.reset()
        rocket.vbo!!.bufferData(worldrenderer.byteBuffer)
    }

    override fun getEntityTexture(entity: EntityRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}