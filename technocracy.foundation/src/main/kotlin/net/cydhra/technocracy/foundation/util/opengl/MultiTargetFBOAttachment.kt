package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

/**
 * Attaches a second render target to an already existing fbo
 */
class MultiTargetFBOAttachment {

    var width: Int = -1
    var height: Int = -1
    var textureID: Int = -1

    companion object {
        val attachments = BufferUtils.createIntBuffer(2)
        init {
            attachments.put(GL30.GL_COLOR_ATTACHMENT0)
            attachments.put(GL30.GL_COLOR_ATTACHMENT1)
            attachments.flip()
        }
    }

    var framebuffer: Framebuffer? = null
        set(value) {
            field = value
            if (width != value!!.framebufferTextureWidth || height != value.framebufferTextureHeight) {
                updateTextureSize()
            }
        }

    fun attachToFBO(autoBindBuffer: Boolean = false) {
        if (autoBindBuffer)
            framebuffer?.bindFramebuffer(false)

        //todo might break optifine?

        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, textureID, 0)
        GL20.glDrawBuffers(attachments)

        if (autoBindBuffer)
            framebuffer?.unbindFramebuffer()
    }

    fun detachFromFBO(autoBindBuffer: Boolean = false) {
        if (autoBindBuffer)
            framebuffer?.bindFramebuffer(false)

        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, 0, 0)
        GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0)

        if (autoBindBuffer)
            framebuffer?.unbindFramebuffer()
    }

    private fun updateTextureSize() {
        if(framebuffer == null)
            return

        width = framebuffer!!.framebufferTextureWidth
        height = framebuffer!!.framebufferTextureHeight

        if (textureID != -1) {
            TextureUtil.deleteTexture(textureID)
        }

        textureID = TextureUtil.glGenTextures()

        GlStateManager.bindTexture(textureID)

        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP)
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP)

        GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA16, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, null)

        GlStateManager.bindTexture(0)
    }
}