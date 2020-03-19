package net.cydhra.technocracy.foundation.util

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.util.vector.Vector4f

fun Framebuffer?.validate(width: Int = Minecraft.getMinecraft().displayWidth, height: Int = Minecraft.getMinecraft().displayHeight, depth: Boolean = true): Framebuffer {
    if (this == null || this.framebufferWidth != width || this.framebufferHeight != height) {
        this?.deleteFramebuffer()
        return Framebuffer(width, height, depth)
    }
    return this
}

fun Framebuffer?.validateAndClear(width: Int = Minecraft.getMinecraft().displayWidth, height: Int = Minecraft.getMinecraft().displayHeight, depth: Boolean = true, viewport: Boolean = true): Framebuffer {
    return validate(width, height, depth).apply {
        framebufferClear()
        bindFramebuffer(viewport)
    }
}

fun BufferBuilder.pos(x: Float, y: Float, z: Float): BufferBuilder {
    return this.pos(x.toDouble(), y.toDouble(), z.toDouble())
}

fun BufferBuilder.color(rgb: Vector4f, alpha: Float): BufferBuilder {
    return this.color(rgb.x, rgb.y, rgb.z, alpha)
}