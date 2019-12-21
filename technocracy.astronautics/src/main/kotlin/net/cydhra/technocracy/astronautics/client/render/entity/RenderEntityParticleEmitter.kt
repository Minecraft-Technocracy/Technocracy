package net.cydhra.technocracy.astronautics.client.render.entity

import net.cydhra.technocracy.astronautics.content.entity.EntityParticleEmitter
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation


class RenderEntityParticleEmitter(renderManagerIn: RenderManager) : Render<EntityParticleEmitter>(renderManagerIn)  {

    override fun doRender(entity: EntityParticleEmitter, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
    }

    override fun getEntityTexture(entity: EntityParticleEmitter): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}