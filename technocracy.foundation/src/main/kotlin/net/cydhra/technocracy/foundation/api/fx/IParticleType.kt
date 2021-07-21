package net.cydhra.technocracy.foundation.api.fx

import com.google.common.util.concurrent.Monitor
import java.util.stream.Stream

/**
 * Technocracy style particles. Those are not rendered and managed by Minecraft, as Minecraft's particle system
 * cannot handle as many particles as this system and is incapable of shader pipelines and other hardware support.
 */
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
     * if not null [uploadBuffers] will be called
     */
    val mutex: Monitor?

    /**
     * called once before this type gets rendered
     */
    fun preRenderType()

    /**
     * called async once before world rendering to allow buffer uploads
     */
    fun uploadBuffers(particles: Stream<AbstractParticle>, partialTicks: Float) {}

    /**
     * called once if [IParticleType.perParticleRender] is set to true
     * @param particles all particles for rendering in batch
     * @param partialTicks current partialTicks
     *
     * @return the amount of rendered particles used for display in [net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager.addDebugInfo]
     */
    fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {
        throw UnsupportedOperationException("This particle cannot be rendered per particle")
    }

    /**
     * called once after this type gets rendered
     */
    fun postRenderType()
}