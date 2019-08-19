package net.cydhra.technocracy.astronautics.client.render.entity

import net.cydhra.technocracy.astronautics.entity.EntityRocket
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper


class RenderEntityRocket(renderManagerIn: RenderManager) : Render<EntityRocket>(renderManagerIn) {

    override fun doRender(entity: EntityRocket, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {

        for(info in entity.template.blocks) {

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
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }

    override fun getEntityTexture(entity: EntityRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}