package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11


class FBOTarget {
    val attachment: ColorAttachment
    val internalFormat: Int
    val format: Int
    val dataType: Int

    var textureFilter: Int = GL11.GL_NEAREST
    var textureWrap: Int = GL11.GL_CLAMP

    val taregtType: TargetType

    var textureID: () -> Int

    private var fixedTextureID = -1

    constructor(
        attachment: ColorAttachment,
        internalFormat: Int = GL11.GL_RGBA8,
        format: Int = GL11.GL_RGBA,
        dataType: Int = GL11.GL_BYTE
    ) {
        this.attachment = attachment
        this.internalFormat = internalFormat
        this.format = format
        this.dataType = dataType

        textureID = { fixedTextureID }

        taregtType = TargetType.NEW
    }

    constructor(other: () -> Framebuffer, attachment: ColorAttachment) {
        this.attachment = attachment
        internalFormat = -1
        format = -1
        dataType = -1

        textureID = { other().framebufferTexture }

        taregtType = TargetType.REFERENCE
    }

    fun setTextureId(id: Int) {
        fixedTextureID = id
    }

    enum class TargetType {
        REFERENCE, NEW
    }
}

enum class ColorAttachment(val id: Int) {
    ATTACHMENT0(36064),
    ATTACHMENT1(36065),
    ATTACHMENT2(36066),
    ATTACHMENT3(36067),
    ATTACHMENT4(36068),
    ATTACHMENT5(36069),
    ATTACHMENT6(36070),
    ATTACHMENT7(36071),
    ATTACHMENT8(36072),
    ATTACHMENT9(36073),
    ATTACHMENT10(36074),
    ATTACHMENT11(36075),
    ATTACHMENT12(36076),
    ATTACHMENT13(36077),
    ATTACHMENT14(36078),
    ATTACHMENT15(36079),
}