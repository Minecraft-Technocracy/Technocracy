package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad

class QuadTinter : IQuadConsumer {
    override lateinit var  origQuad: BakedQuad
    override lateinit var  unmodifiedQuad: SimpleQuad

    var tint = -1

    override fun reset() {
        tint = -1
    }

    override fun consume(quad: SimpleQuad) {
        if (origQuad!!.hasTintIndex()) {
            quad.tintColor = tint
        }
    }
}