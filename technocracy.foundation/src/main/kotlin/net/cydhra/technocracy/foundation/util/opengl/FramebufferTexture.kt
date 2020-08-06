package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11

/**
 * Can copy the texture of the provided Framebuffer
 */
class FramebufferTexture : AbstractTexture() {

    var width = -1
    var height = -1

    fun load(buffer: Framebuffer) {
        if (width != buffer.framebufferTextureWidth || height != buffer.framebufferTextureHeight) {
            width = buffer.framebufferTextureWidth
            height = buffer.framebufferTextureHeight
            deleteGlTexture()
            TextureUtil.allocateTexture(getGlTextureId(), width, height)
        }
        buffer.bindFramebuffer(false)
        GlStateManager.bindTexture(getGlTextureId())
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, 0, 0, this.width, this.height, 0)
    }

    override fun loadTexture(resourceManager: IResourceManager) {
    }
}