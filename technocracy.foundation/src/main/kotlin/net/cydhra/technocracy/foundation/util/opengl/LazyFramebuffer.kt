package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11


typealias Framebuffer = LazyFramebuffer


fun hackyFramebufferInit(): Boolean {
    val lastState = Minecraft.getMinecraft().gameSettings.fboEnable
    Minecraft.getMinecraft().gameSettings.fboEnable = false
    return lastState
}

/**
 * the constructor basically disables framebuffers using hackyFramebufferInit before the super constructor of the Framebuffer class gets called,
 * so that no fbo object is generated. Then we just revert the field to its original state in the init block
 *
 * we do this so we have more controll over when we actually want to create our framebuffer
 */
class LazyFramebuffer private constructor(initializer: (LazyFramebuffer.() -> Unit)? = null, tmp: Boolean) :
    Framebuffer(-1, -1, false) {

    constructor(initializer: (LazyFramebuffer.() -> Unit)? = null) : this(initializer, hackyFramebufferInit())

    init {
        Minecraft.getMinecraft().gameSettings.fboEnable = tmp

        framebufferFilter = GL11.GL_NEAREST
        initializer?.invoke(this)
    }

    var textureWrapper = GL11.GL_CLAMP

    fun refreshFramebuffer(keepTexture: Boolean) {
        if (OpenGlHelper.isFramebufferEnabled() && framebufferObject != -1) {
            unbindFramebufferTexture()
            unbindFramebuffer()
            if (depthBuffer > -1) {
                OpenGlHelper.glDeleteRenderbuffers(depthBuffer)
                depthBuffer = -1
            }
            if (framebufferTexture > -1 && !keepTexture) {
                TextureUtil.deleteTexture(framebufferTexture)
                framebufferTexture = -1
            }
        }
    }

    /**
     * validates the framebuffer, so it is ready to be used
     *
     * @param width the width of the framebuffer
     * @param height the height of the framebuffer
     * @param depth should the framebuffer have a depth buffer attachment
     * @param texture if set to something other then -1 it will write onto this texture
     */
    fun validate(
        width: Int = Minecraft.getMinecraft().displayWidth,
        height: Int = Minecraft.getMinecraft().displayHeight,
        depth: Boolean = true,
        texture: Int = -1
    ): LazyFramebuffer {
        if (this.framebufferTextureWidth != width || this.framebufferTextureHeight != height || (texture != -1 && this.framebufferTexture != texture) || depth != useDepth) {
            useDepth = depth
            this.refreshFramebuffer(texture != -1)

            GlStateManager.enableDepth()
            createFramebuffer(width, height)
            checkFramebufferComplete()
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0)
        }
        return this
    }

    /**
     * validates and clears the framebuffer, so it is ready to be used
     *
     * @param width the width of the framebuffer
     * @param height the height of the framebuffer
     * @param depth should the framebuffer have a depth buffer attachment
     * @param bind should the frambuffer be bound after this
     * @param viewport should the viewport be changed if the buffer is bound. Only used if [bind] is set to true
     * @param texture if set to something other then -1 it will write onto this texture
     */
    fun validateAndClear(
        width: Int = Minecraft.getMinecraft().displayWidth,
        height: Int = Minecraft.getMinecraft().displayHeight,
        depth: Boolean = true,
        bind: Boolean = false,
        viewport: Boolean = true,
        texture: Int = -1
    ): LazyFramebuffer {
        return validate(width, height, depth, texture).apply {
            framebufferClear()
            if (bind)
                bindFramebuffer(viewport)
        }
    }

    override fun setFramebufferFilter(ignored: Int) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            //framebufferFilter =  framebufferFilterIn
            GlStateManager.bindTexture(framebufferTexture)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, framebufferFilter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, framebufferFilter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrapper)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrapper)
            GlStateManager.bindTexture(0)
        }
    }
}