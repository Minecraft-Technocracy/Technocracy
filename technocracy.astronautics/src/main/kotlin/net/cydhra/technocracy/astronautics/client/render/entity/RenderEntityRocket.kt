package net.cydhra.technocracy.astronautics.client.render.entity

import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.foundation.util.structures.TemplateRenderHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11


class RenderEntityRocket(renderManagerIn: RenderManager) : Render<EntityRocket>(renderManagerIn) {

    override fun doRender(entity: EntityRocket, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        entity.vbo = TemplateRenderHelper.generateVBO(entity.template, entity.vbo, entity.position)

        val intX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks
        val intY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks
        val intZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks

        GL11.glColor4f(1f,1f,1f, 0.2f)
        TemplateRenderHelper.renderVBO(entity.vbo, intX, intY, intZ)
    }

    override fun getEntityTexture(entity: EntityRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}