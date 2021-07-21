package net.cydhra.technocracy.foundation.util.model.pipeline

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.minecraft.client.renderer.block.model.BakedQuad


/**
 * An Interface to build a pipeline that can modify a [SimpleQuad]
 */
interface IQuadConsumer {
    /**
     * consumes the Quad and can modify it
     */
    fun consume(quad: SimpleQuad)

    /**
     * Will be called once when the Consumer is added to the pipeline
     * any other cleanup calls needs to be done manually
     */
    fun reset()

    /**
     * The Original BakedQuad
     */
    var origQuad: BakedQuad

    /**
     * An unmodified Version of the quad that gets pumped into [consume]
     */
    var unmodifiedQuad: SimpleQuad
}