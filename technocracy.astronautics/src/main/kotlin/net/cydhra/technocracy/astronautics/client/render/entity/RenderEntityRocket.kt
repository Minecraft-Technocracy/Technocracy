package net.cydhra.technocracy.astronautics.client.render.entity

import net.cydhra.technocracy.astronautics.entity.EntityRocket
import net.cydhra.technocracy.foundation.util.WrappedState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockFluidRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.crash.CrashReport
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.ReportedException
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11


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

        entity.generateVBO()

        val mc = Minecraft.getMinecraft()

        GL11.glPushMatrix()

        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)


        val intX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
        val intY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
        val intZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ

        GL11.glTranslated(intX - 0.5, intY, intZ - 0.5)

        mc.entityRenderer.enableLightmap()


        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit)
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY)
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

        GL11.glPopMatrix()
    }

    override fun getEntityTexture(entity: EntityRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}