package net.cydhra.technocracy.foundation.util.model.pipeline

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.minecraft.client.renderer.block.model.BakedQuad


/**
 * An Interface to build a pipeline that can modify a [SimpleQuad]
 */
interface IQuadConsumer {
    fun consume(quad: SimpleQuad)

    fun reset()

    var origQuad: BakedQuad
    var unmodifiedQuad: SimpleQuad
}