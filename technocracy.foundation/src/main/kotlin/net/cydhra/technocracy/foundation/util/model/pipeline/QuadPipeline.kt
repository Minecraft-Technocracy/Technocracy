package net.cydhra.technocracy.foundation.util.model.pipeline

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.minecraft.client.renderer.block.model.BakedQuad


/**
 * A pipeline that applies
 */
class QuadPipeline {
    private val consumers = mutableListOf<IQuadConsumer>()

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
        var unmod = SimpleQuad(quad)
        for (cons in consumers) {
            cons.origQuad = origQuad
            cons.unmodifiedQuad = unmod
            cons.consume(quad)
            unmod = cons.unmodifiedQuad
        }
        return quad
    }

    fun pipe(quad: SimpleQuad): SimpleQuad {
        var unmod = SimpleQuad(quad)
        for (cons in consumers) {
            cons.unmodifiedQuad = unmod
            cons.consume(quad)
            unmod = cons.unmodifiedQuad
        }
        return quad
    }
}