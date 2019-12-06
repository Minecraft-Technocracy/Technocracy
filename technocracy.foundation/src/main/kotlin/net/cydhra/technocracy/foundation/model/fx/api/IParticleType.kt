package net.cydhra.technocracy.foundation.model.fx.api


interface IParticleType {
    val name: String
    fun preRenderType()
    fun postRenderType()
}