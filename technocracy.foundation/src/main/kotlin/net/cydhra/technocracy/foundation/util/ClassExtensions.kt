package net.cydhra.technocracy.foundation.util

import net.cydhra.technocracy.foundation.util.opengl.MultiTargetFBO
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityBoat
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.util.vector.Vector4f
import java.util.*

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

/**
 * Body as in 3/4 of its hitbox
 */
fun Entity.isBodyInsideOfMaterial(materialIn: Material): Boolean {
    return if (ridingEntity is EntityBoat) {
        false
    } else {
        val d0 = posY + this.eyeHeight.toDouble()
        val blockpos = BlockPos(posX, d0, posZ)
        val iblockstate = world.getBlockState(blockpos)
        val result = iblockstate.block.isEntityInsideMaterial(world, blockpos, iblockstate, this, d0, materialIn, true)
        if (result != null) return result
        if (iblockstate.material === materialIn) {
            ForgeHooks.isInsideOfMaterial(materialIn, this, blockpos)
        } else {
            false
        }
    }
}

fun intFromBools(vararg bools: Boolean): Int {
    if (bools.size > 32) throw ArrayIndexOutOfBoundsException("varags too long")
    var i = 0
    for ((index, b) in bools.withIndex()) {
        i = i or ((1 and b) shl index)
    }
    return i
}

infix fun Int.and(other: Boolean): Int {
    return if (other) this else 0
}

fun longFromBools(vararg bools: Boolean): Long {
    if (bools.size > 64) throw ArrayIndexOutOfBoundsException("varags too long")
    var i = 0L
    for ((index, b) in bools.withIndex()) {
        i = i or ((1L and b) shl index)
    }
    return i
}

infix fun Long.and(other: Boolean): Long {
    return if (other) this else 0
}