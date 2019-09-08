package net.cydhra.technocracy.astronautics.client.util

import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import java.awt.image.BufferedImage


class FixedTexture(val image: BufferedImage) : AbstractTexture() {
    override fun loadTexture(resourceManager: IResourceManager) {
        this.deleteGlTexture()
        TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, false, false)
    }
}