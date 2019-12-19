package net.cydhra.technocracy.foundation.model.fx.api

import java.lang.IllegalStateException
import java.util.stream.Stream


interface IParticleType {
    val name: String
    val perParticleRender: Boolean
    fun preRenderType()
    fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {
        throw IllegalStateException("Not Implemented")
    }

    fun postRenderType()
}