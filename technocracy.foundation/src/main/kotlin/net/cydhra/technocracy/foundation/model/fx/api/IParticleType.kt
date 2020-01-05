package net.cydhra.technocracy.foundation.model.fx.api

import java.lang.IllegalStateException
import java.util.stream.Stream


interface IParticleType {
    /**
     * name of the Particle
     */
    val name: String

    /**
     * should the particle be rendered per instance or per group
     */
    val perParticleRender: Boolean

    /**
     * maximum amount of particles that can be rendered at once
     * -1 for unlimited
     */
    val maxParticles: Int

    /**
     * called once before this type gets rendered
     */
    fun preRenderType()

    /**
     * called once if [IParticleType.maxParticles] is set to true
     * @param particles all particles for rendering in batch
     * @param partialTicks current partialTicks
     *
     * @return the amount of rendered particles used for display in [net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager.addDebugInfo]
     */
    fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {
        throw IllegalStateException("Not Implemented")
    }

    /**
     * called once after this type gets rendered
     */
    fun postRenderType()
}