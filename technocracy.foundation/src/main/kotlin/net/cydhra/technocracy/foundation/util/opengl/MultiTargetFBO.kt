package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30


class MultiTargetFBO(framebufferIn: Framebuffer) {

    var width: Int = -1
    var height: Int = -1

    var textureID: Int = -1

    var framebuffer: Framebuffer = framebufferIn
        set(value) {
            field = value
            if (width != value.framebufferTextureWidth || height != value.framebufferTextureHeight) {
                updateTextureSize()
            }
        }

    fun attachToFBO(autoBindBuffer: Boolean = false) {
        if (autoBindBuffer)
            framebuffer.bindFramebuffer(false)

        //todo meight break optifine?

        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, textureID, 0)

        if (autoBindBuffer)
            framebuffer.unbindFramebuffer()
    }

    fun detachFromFBO(autoBindBuffer: Boolean = false) {
        if (autoBindBuffer)
            framebuffer.bindFramebuffer(false)

        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, 0, 0)

        if (autoBindBuffer)
            framebuffer.unbindFramebuffer()
    }

    private fun updateTextureSize() {
        width = framebuffer.framebufferTextureWidth
        height = framebuffer.framebufferTextureHeight

        if (textureID != -1) {
            TextureUtil.deleteTexture(textureID)
        }

        textureID = TextureUtil.glGenTextures()

        GlStateManager.bindTexture(textureID)

        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP)
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP)

        GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null)

        GlStateManager.bindTexture(0)
    }
}