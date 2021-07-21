package net.cydhra.technocracy.foundation.util.model.pipeline.consumer.clone

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad


class QuadCloneConsumer(var clonePos: Boolean) : IQuadConsumer {
    override lateinit var origQuad: BakedQuad
    override lateinit var unmodifiedQuad: SimpleQuad

    override fun reset() {
    }

    override fun consume(quad: SimpleQuad) {
        if (origQuad == null)
            return
        val bakedQuad = origQuad

        quad.face = bakedQuad.face
        quad.tintIndex = bakedQuad.tintIndex
        quad.sprite = bakedQuad.sprite
        quad.applyDiffuseLighting = bakedQuad.shouldApplyDiffuseLighting()
        quad.tintColor = 0

        if(quad.arraysInit) {
            quad.vertColor.clear()
            quad.vertLight.clear()
            quad.vertUv.clear()
            quad.vertNormal.clear()
            //quad.data.clear()

            if (clonePos) {
                quad.vertPos.clear()
            }
        }

        bakedQuad.pipe(QuadCloneVertexConsumer(quad, clonePos))
        unmodifiedQuad = SimpleQuad(quad)
    }
}