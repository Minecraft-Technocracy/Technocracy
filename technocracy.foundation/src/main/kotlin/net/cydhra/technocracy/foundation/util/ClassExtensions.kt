package net.cydhra.technocracy.foundation.util

import net.cydhra.technocracy.foundation.util.opengl.MultiTargetFBO
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.Entity
import net.minecraft.world.World
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.fml.relauncher.Side
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

fun MultiTargetFBO?.validate(width: Int, height: Int, ownDepth: Boolean = false, hdrFrameBuffer: Boolean = false, scale: Float = 1f): MultiTargetFBO {
    return if (this != null && this.width == width && this.height == height) {
        this
    } else {
        this?.deleteFramebuffer()
        val tmp = MultiTargetFBO(width, height, ownDepth, hdrFrameBuffer, scale)
        tmp.createFramebuffer()
        tmp
    }.updateDepth()
}

fun MultiTargetFBO?.validate(framebuffer: Framebuffer, ownDepth: Boolean = false, hdrFrameBuffer: Boolean = false, scale: Float = 1f): MultiTargetFBO {
    return if (this != null && width == framebuffer.framebufferWidth && height == framebuffer.framebufferHeight) {
        this
    } else {
        this?.deleteFramebuffer()
        val tmp = MultiTargetFBO(framebuffer, ownDepth, hdrFrameBuffer, scale)
        tmp.createFramebuffer()
        tmp
    }.updateDepth()
}

fun BufferBuilder.pos(x: Float, y: Float, z: Float): BufferBuilder {
    return this.pos(x.toDouble(), y.toDouble(), z.toDouble())
}

fun BufferBuilder.color(rgb: Vector4f, alpha: Float): BufferBuilder {
    return this.color(rgb.x, rgb.y, rgb.z, alpha)
}

fun World.getSide(): Side {
    return if (this.isRemote) Side.CLIENT else Side.SERVER
}

fun Entity.getSide(): Side {
    return this.world.getSide()
}

fun EntityEvent.getSide(): Side {
    return entity.world.getSide()
}