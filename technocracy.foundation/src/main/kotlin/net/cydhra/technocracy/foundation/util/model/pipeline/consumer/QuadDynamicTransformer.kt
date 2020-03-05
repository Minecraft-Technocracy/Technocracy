package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import java.util.function.BiConsumer


class QuadDynamicTransformer(val callBack: BiConsumer<QuadDynamicTransformer, SimpleQuad>) : IQuadConsumer {
    override lateinit var  origQuad: BakedQuad
    override lateinit var  unmodifiedQuad: SimpleQuad

    override fun consume(quad: SimpleQuad) {
        callBack.accept(this, quad)
    }

    override fun reset() {}
}