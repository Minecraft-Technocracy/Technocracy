package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.MinecraftError
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 * Generates a image of the provided Framebuffer
 */
class DepthTexture(name: String) : AbstractTexture() {

    init {
        Minecraft.getMinecraft().textureManager.loadTexture(ResourceLocation("technocracy.foundation", name), this)
    }

    var width = -1
    var height = -1

    override fun loadTexture(resourceManager: IResourceManager) {}

    fun load(buffer: Framebuffer) {
        if (width != buffer.framebufferTextureWidth || height != buffer.framebufferTextureHeight) {
            width = buffer.framebufferTextureWidth
            height = buffer.framebufferTextureHeight
            deleteGlTexture()
            TextureUtil.allocateTexture(getGlTextureId(), width, height)

            GlStateManager.bindTexture(getGlTextureId())
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP)
        }
        buffer.bindFramebuffer(false)
        GlStateManager.bindTexture(getGlTextureId())
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, this.width, this.height, 0);
    }
}