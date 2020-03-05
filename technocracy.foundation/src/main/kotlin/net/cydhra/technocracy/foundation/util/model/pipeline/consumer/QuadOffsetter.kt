package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad


object QuadOffsetter : IQuadConsumer {

    override lateinit var  origQuad: BakedQuad
    override lateinit var  unmodifiedQuad: SimpleQuad

    var offsetX: Float = 0f
    var offsetY: Float = 0f
    var offsetZ: Float = 0f

    override fun consume(quad: SimpleQuad) {
        for (vertPo in quad.vertPos) {
            vertPo.x += offsetX
            vertPo.y += offsetY
            vertPo.z += offsetZ
        }
    }

    override fun reset() {
        offsetX = 0f
        offsetY = 0f
        offsetZ = 0f
    }
}