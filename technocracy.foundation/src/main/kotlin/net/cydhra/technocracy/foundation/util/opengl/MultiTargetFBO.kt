package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*

/**
 * A FBO with 2 color render targets
 */
class MultiTargetFBO(var width: Int, var height: Int, var useDepth: Boolean, var hdrFrameBuffer: Boolean = false, var scale: Float = 1f) {

    constructor(sharedBuffers: Framebuffer, ownDepth: Boolean = false, hdrFrameBuffer: Boolean = false, scale: Float = 1f) : this(sharedBuffers.framebufferWidth, sharedBuffers.framebufferHeight, ownDepth || sharedBuffers.useDepth, hdrFrameBuffer, scale) {
        this.parentBuffer = sharedBuffers
        this.ownDepth = ownDepth
        generateBuffers = false
    }

    private var generateBuffers = true
    private var parentBuffer: Framebuffer? = null
    private var ownDepth = true

    var framebufferObject: Int = 0
    var textureOne: Int = 0
    var depthBuffer: Int = 0
    var depthTexture: Int = 0

    var textureTwo: Int = -1

    companion object {
        val attachments = BufferUtils.createIntBuffer(2)

        init {
            attachments.put(GL30.GL_COLOR_ATTACHMENT0)
            attachments.put(GL30.GL_COLOR_ATTACHMENT1)
            attachments.flip()
        }
    }

    fun createFramebuffer(): MultiTargetFBO {
        if (this.framebufferObject >= 0) {
            this.deleteFramebuffer()
        }

        this.framebufferObject = OpenGlHelper.glGenFramebuffers()
        this.textureOne = parentBuffer?.framebufferTexture ?: TextureUtil.glGenTextures();
        this.textureTwo = TextureUtil.glGenTextures();

        if (ownDepth || parentBuffer == null) {
            this.depthBuffer = OpenGlHelper.glGenRenderbuffers()
        } else {
            this.depthBuffer = parentBuffer!!.depthBuffer
        }

        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject)
        this.setFramebufferFilter(this.textureOne, parentBuffer?.framebufferFilter ?: GL11.GL_NEAREST)
        GlStateManager.bindTexture(this.textureOne)

        if (parentBuffer == null)
            setupTexture()

        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.textureOne, 0)

        this.setFramebufferFilter(this.textureTwo, GL11.GL_NEAREST)
        GlStateManager.bindTexture(this.textureTwo)
        setupTexture()
        //GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null)
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, this.textureTwo, 0)

        if (this.useDepth) {
            depthTexture = TextureUtil.glGenTextures()
            TextureUtil.allocateTexture(depthTexture, width, height)

            OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer)
            OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, 33190, this.width, this.height)
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer)
        }

        this.framebufferClear()
        GlStateManager.bindTexture(0)

        checkFramebufferComplete()

        return this
    }

    private fun setupTexture(useHDR: Boolean = hdrFrameBuffer) {

        val width = ((width + scale - 1) / scale).toInt()
        val height = ((height + scale - 1) / scale).toInt()

        if (useHDR) {
            GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB16, width, (height * scale).toInt(), 0, GL11.GL_RGB, GL11.GL_FLOAT, null)
        } else {
            GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, (height * scale).toInt(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null)
        }
    }

    fun updateDepth(): MultiTargetFBO {
        if (useDepth) {
            if (ownDepth || parentBuffer == null) {
                bindFramebuffer(false)
            } else {
                parentBuffer!!.bindFramebuffer(false)
            }

            GlStateManager.bindTexture(depthTexture)
            GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, this.width, this.height, 0)

            unbindFramebuffer()
        }
        return this
    }

    private fun checkFramebufferComplete() {
        val i = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER)

        if (i != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
            if (i == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT) {
                throw RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT")
            } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH) {
                throw RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT")
            } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER) {
                throw RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER")
            } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER) {
                throw RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER")
            } else {
                throw RuntimeException("glCheckFramebufferStatus returned unknown status:$i")
            }
        }
    }

    fun bindFramebuffer(viewPort: Boolean) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject)

            if (viewPort) {
                GlStateManager.viewport(0, 0, (width * scale).toInt(), (height * scale).toInt())
            }
        }
    }

    fun unbindFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0)
        }
    }

    fun framebufferClear() {
        this.bindFramebuffer(true)

        if (parentBuffer != null) {
            TCOpenGlHelper.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT1)
        } else {
            TCOpenGlHelper.glDrawBuffers(attachments)
        }

        GlStateManager.clearColor(0f, 0f, 0f, 1f)
        var i = GL11.GL_COLOR_BUFFER_BIT

        if (this.useDepth && ownDepth) {
            GlStateManager.clearDepth(1.0)
            i = i or GL11.GL_DEPTH_BUFFER_BIT
        }

        GlStateManager.clear(i)

        if (parentBuffer != null) {
            TCOpenGlHelper.glDrawBuffers(attachments)
        }

        this.unbindFramebuffer()
    }

    fun deleteFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(0)
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject)

            if (this.depthBuffer > -1 && generateBuffers) {
                OpenGlHelper.glDeleteRenderbuffers(this.depthBuffer)
                this.depthBuffer = -1
            }

            if (this.textureOne > -1 && generateBuffers) {
                TextureUtil.deleteTexture(this.textureOne)
                this.textureOne = -1
            }

            if (this.depthTexture > -1) {
                TextureUtil.deleteTexture(this.depthTexture)
                this.depthTexture = -1
            }

            if (this.textureTwo > -1) {
                TextureUtil.deleteTexture(this.textureTwo)
                this.textureTwo = -1
            }

            if (this.framebufferObject > -1) {
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0)
                OpenGlHelper.glDeleteFramebuffers(this.framebufferObject)
                this.framebufferObject = -1
            }
        }
    }

    fun setFramebufferFilter(texture: Int, filter: Int) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(texture)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP)
            GlStateManager.bindTexture(0)
        }
    }

    fun validate(width: Int, height: Int): MultiTargetFBO {
        return if (this.width == width && this.height == height) {
            this
        } else {
            this.deleteFramebuffer()
            val tmp = MultiTargetFBO(width, height, useDepth, hdrFrameBuffer)
            tmp.createFramebuffer()
            tmp
        }.updateDepth()
    }

    fun validate(framebuffer: Framebuffer): MultiTargetFBO {
        return if (width == framebuffer.framebufferWidth && height == framebuffer.framebufferHeight) {
            this
        } else {
            this.deleteFramebuffer()
            val tmp = MultiTargetFBO(framebuffer, ownDepth, hdrFrameBuffer, scale)
            tmp.createFramebuffer()
            tmp
        }.updateDepth()
    }

}