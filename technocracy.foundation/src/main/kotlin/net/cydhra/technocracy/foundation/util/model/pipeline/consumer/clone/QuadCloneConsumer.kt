package net.cydhra.technocracy.foundation.util.model.pipeline.consumer.clone

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.QuadFacadeTransformer
import net.minecraft.client.renderer.block.model.BakedQuad


object QuadCloneConsumer : IQuadConsumer {
    override var origQuad: BakedQuad? = null
    override var unmodifiedQuad: SimpleQuad? = null
    var clonePos: Boolean = false

    override fun reset() {
        clonePos = false
        origQuad = null
    }

    override fun consume(quad: SimpleQuad) {
        if (origQuad == null)
            return
        val bakedQuad = origQuad!!

        quad.face = bakedQuad.face
        quad.tintIndex = bakedQuad.tintIndex
        quad.sprite = bakedQuad.sprite
        quad.applyDiffuseLighting = bakedQuad.shouldApplyDiffuseLighting()
        quad.tintColor = 0
        quad.vertColor.clear()
        quad.vertLight.clear()
        quad.vertUv.clear()
        quad.vertNormal.clear()
        quad.data.clear()

        if(clonePos) {
            quad.vertPos.clear()
        }

        bakedQuad.pipe(QuadCloneVertexConsumer(quad, clonePos))
        unmodifiedQuad = SimpleQuad(quad)
    }
}