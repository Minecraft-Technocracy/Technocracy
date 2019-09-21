package net.cydhra.technocracy.foundation.util.model.pipeline

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.minecraft.client.renderer.block.model.BakedQuad


class QuadPipeline {
    val consumers = mutableSetOf<IQuadConsumer>()

    fun addConsumer(vararg consumer: IQuadConsumer): QuadPipeline {
        for (cons in consumer)
            cons.reset()
        consumers.addAll(consumer)
        return this
    }

    fun removeConsumer(vararg consumer: IQuadConsumer): QuadPipeline {
        consumers.removeAll(consumer)
        return this
    }

    fun pipe(quad: SimpleQuad, origQuad: BakedQuad): SimpleQuad {
        for (cons in consumers) {
            cons.origQuad = origQuad
            cons.consume(quad)
        }
        return quad
    }

    fun pipe(quad: SimpleQuad): SimpleQuad {
        for (cons in consumers) {
            cons.consume(quad)
        }
        return quad
    }
}