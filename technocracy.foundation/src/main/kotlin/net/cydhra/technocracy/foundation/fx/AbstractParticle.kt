package net.cydhra.technocracy.foundation.fx

import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.entity.Entity
import net.minecraft.world.World


abstract class AbstractParticle(worldIn: World, posXIn: Double, posYIn: Double, posZIn: Double) : Particle(worldIn, posXIn, posYIn, posZIn) {

    var rotation: Float = 0.0f
    var size: Float = 1.0f

    abstract fun getType(): IParticleType

    abstract fun renderParticle()

    fun getPosX(): Double {
        return posX
    }
    fun getPosY(): Double {
        return posY
    }

    fun getPosZ(): Double {
        return posZ
    }

    @Deprecated("Unused Method")
    override fun renderParticle(buffer: BufferBuilder, entityIn: Entity, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float) {

    }
}