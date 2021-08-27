package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import java.nio.IntBuffer

class MultiTargetFBO(
    var width: Int,
    var height: Int,
    val depth: DepthTarget,
    vararg val allTargets: FBOTarget
) {

    constructor(
        width: Int,
        height: Int,
        depth: Boolean,
        vararg allTargets: FBOTarget
    ) : this(width, height, DepthTarget(depth), *allTargets)

    var framebufferObject: Int = 0
    var depthTexture: Int = 0

    val ownTargets = mutableListOf<FBOTarget>()

    /**
     * returns the texture id of the target with index i
     */
    operator fun get(int: Int): Int {
        return allTargets[int].textureID()
    }

    val attachments: IntBuffer = BufferUtils.createIntBuffer(allTargets.size)
    val clearableAttachments: IntBuffer

    init {
        for (target in allTargets) {
            attachments.put(target.attachment.id)
            if (target.taregtType == FBOTarget.TargetType.NEW) {
                ownTargets.add(target)
            }
        }

        clearableAttachments = BufferUtils.createIntBuffer(ownTargets.size)

        for (target in ownTargets) {
            clearableAttachments.put(target.attachment.id)
        }

        attachments.flip()
        clearableAttachments.flip()
    }

    fun createFramebuffer(): MultiTargetFBO {
        if (this.framebufferObject >= 0) {
            this.deleteFramebuffer()
        }

        this.framebufferObject = OpenGlHelper.glGenFramebuffers()

        for (target in ownTargets) {
            target.setTextureId(TextureUtil.glGenTextures())
        }

        if (depth.type == DepthTarget.TargetType.NEW) {
            depth.setBufferId(OpenGlHelper.glGenRenderbuffers())
        }

        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject)

        for (target in allTargets) {
            if (target.taregtType == FBOTarget.TargetType.NEW) {
                setFramebufferFilter(target)
                setupTexture(target)
            } else {
                GlStateManager.bindTexture(target.textureID())
            }

            OpenGlHelper.glFramebufferTexture2D(
                OpenGlHelper.GL_FRAMEBUFFER,
                target.attachment.id,
                GL11.GL_TEXTURE_2D,
                target.textureID(),
                0
            )
        }

        if (depth.type != DepthTarget.TargetType.NONE) {
            depthTexture = TextureUtil.glGenTextures()
            TextureUtil.allocateTexture(depthTexture, width, height)

            OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, depth.bufferID())

            OpenGlHelper.glRenderbufferStorage(
                OpenGlHelper.GL_RENDERBUFFER,
                GL14.GL_DEPTH_COMPONENT24,
                this.width,
                this.height
            )
            OpenGlHelper.glFramebufferRenderbuffer(
                OpenGlHelper.GL_FRAMEBUFFER,
                OpenGlHelper.GL_DEPTH_ATTACHMENT,
                OpenGlHelper.GL_RENDERBUFFER,
                depth.bufferID()
            )
        }

        this.framebufferClear()
        GlStateManager.bindTexture(0)

        checkFramebufferComplete()

        return this
    }

    private fun setupTexture(taregt: FBOTarget) {
        GlStateManager.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            taregt.internalFormat,
            width,
            height,
            0,
            taregt.format,
            taregt.dataType,
            null
        )
    }

    fun updateDepth(): MultiTargetFBO {
        if (depth.type != DepthTarget.TargetType.NONE) {
            bindFramebuffer(false)

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
                GlStateManager.viewport(0, 0, width, height)
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

        TCOpenGlHelper.glDrawBuffers(this.clearableAttachments)

        GlStateManager.clearColor(0f, 0f, 0f, 0f)
        var i = GL11.GL_COLOR_BUFFER_BIT

        if (depth.type == DepthTarget.TargetType.NEW) {
            GlStateManager.clearDepth(1.0)
            i = i or GL11.GL_DEPTH_BUFFER_BIT
        }

        GlStateManager.clear(i)

        TCOpenGlHelper.glDrawBuffers(this.attachments)

        this.unbindFramebuffer()
    }

    fun refreshFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(0)
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject)

            for (target in ownTargets) {
                val tmp = target.textureID()
                if (tmp > -1) {
                    TextureUtil.deleteTexture(tmp)
                    target.setTextureId(-1)
                }
            }

            if (this.depthTexture > -1) {
                TextureUtil.deleteTexture(this.depthTexture)
                this.depthTexture = -1
            }
        }
    }

    fun validate(
        width: Int = Minecraft.getMinecraft().displayWidth,
        height: Int = Minecraft.getMinecraft().displayHeight
    ): MultiTargetFBO {
        if (this.width != width || this.height != height) {
            this.refreshFramebuffer()
            this.width = width
            this.height = height
            //TODO this actually will delete the fbo anyways so replace with own
            createFramebuffer()
        }
        return this
    }

    fun validateAndClear(
        width: Int = Minecraft.getMinecraft().displayWidth,
        height: Int = Minecraft.getMinecraft().displayHeight,
        viewport: Boolean = true,
        bind: Boolean = true
    ): MultiTargetFBO {
        return validate(width, height).apply {
            framebufferClear()
            if (bind)
                bindFramebuffer(viewport)
        }
    }

    fun deleteFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(0)
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject)

            if (depth.type == DepthTarget.TargetType.NEW) {
                val tmp = depth.bufferID()
                if (tmp != -1) {
                    OpenGlHelper.glDeleteRenderbuffers(tmp)
                    depth.setBufferId(-1)
                }
            }

            for (target in ownTargets) {
                val tmp = target.textureID()
                if (tmp > -1) {
                    TextureUtil.deleteTexture(tmp)
                    target.setTextureId(-1)
                }
            }

            if (this.depthTexture > -1) {
                TextureUtil.deleteTexture(this.depthTexture)
                this.depthTexture = -1
            }

            if (this.framebufferObject > -1) {
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0)
                OpenGlHelper.glDeleteFramebuffers(this.framebufferObject)
                this.framebufferObject = -1
            }
        }
    }

    fun setFramebufferFilter(target: FBOTarget) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(target.textureID())
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, target.textureFilter)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, target.textureFilter)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, target.textureWrap)
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, target.textureWrap)
        }
    }
}