package net.cydhra.technocracy.foundation.model.fx.api

import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.lwjgl.util.vector.Vector3f
import kotlin.math.absoluteValue


abstract class AbstractParticle(worldIn: World, posXIn: Double, posYIn: Double, posZIn: Double) : Particle(worldIn, posXIn, posYIn, posZIn) {

    var rotation: Float = 0.0f
    var size: Float = 1.0f
    var renderTime = 0f
    var lastDistance = Double.MAX_VALUE

    abstract fun getType(): IParticleType

    abstract fun renderParticle(partialTicks: Float)

    fun onUpdate(renderEntity: Entity): Boolean {
        super.onUpdate()
        if (isAlive)
            lastDistance = renderEntity.getDistanceSq(posX, posY, posZ).absoluteValue
        return isAlive
    }

    fun interpolatePosition(position: Vector3f, partialTicks: Float): Vector3f {
        val manager = Minecraft.getMinecraft().renderManager
        val posX = prevPosX + (posX - prevPosX) * partialTicks
        val posY = prevPosY + (posY - prevPosY) * partialTicks
        val posZ = prevPosZ + (posZ - prevPosZ) * partialTicks
        position.set((posX - manager.viewerPosX).toFloat(), (posY - manager.viewerPosY).toFloat(), (posZ - manager.viewerPosZ).toFloat())
        return position
    }

    fun getPosX(): Double {
        return posX
    }

    fun getPosY(): Double {
        return posY
    }

    fun getPosZ(): Double {
        return posZ
    }

    fun getMaxAge(): Int {
        return particleMaxAge
    }

    fun getAge(): Int {
        return particleAge
    }

    @Deprecated("Unused Method")
    override fun renderParticle(buffer: BufferBuilder, entityIn: Entity, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float) {

    }
}