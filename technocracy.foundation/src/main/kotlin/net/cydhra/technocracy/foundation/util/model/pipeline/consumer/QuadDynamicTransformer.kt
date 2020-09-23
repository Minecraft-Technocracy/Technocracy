package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import java.util.function.BiConsumer


class QuadDynamicTransformer(val callBack: QuadDynamicTransformer.(SimpleQuad) -> Unit) : IQuadConsumer {
    override lateinit var  origQuad: BakedQuad
    override lateinit var  unmodifiedQuad: SimpleQuad

    override fun consume(quad: SimpleQuad) {
        callBack.invoke(this, quad)
    }

    override fun reset() {}
}