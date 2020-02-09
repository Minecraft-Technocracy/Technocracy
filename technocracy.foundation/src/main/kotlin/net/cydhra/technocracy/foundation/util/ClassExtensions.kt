package net.cydhra.technocracy.foundation.util

import net.minecraft.client.Minecraft
import net.minecraft.client.shader.Framebuffer

fun Framebuffer?.validate(width: Int = Minecraft.getMinecraft().displayWidth, height: Int = Minecraft.getMinecraft().displayHeight, depth: Boolean = true): Framebuffer {
    if (this == null || this.framebufferWidth != width || this.framebufferHeight != height) {
        this?.deleteFramebuffer()
        return Framebuffer(width, height, depth)
    }
    return this
}

fun Framebuffer?.validateAndClear(width: Int = Minecraft.getMinecraft().displayWidth, height: Int = Minecraft.getMinecraft().displayHeight, depth: Boolean = true): Framebuffer {
    return validate(width, height, depth).apply {
        framebufferClear()
        bindFramebuffer(true)
    }
}