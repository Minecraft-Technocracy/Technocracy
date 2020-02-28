package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.EnumFacing.AxisDirection.NEGATIVE
import net.minecraft.util.EnumFacing.AxisDirection.POSITIVE
import net.minecraft.util.math.MathHelper


object QuadShrinker : IQuadConsumer {

    override var origQuad: BakedQuad? = null
    override var unmodifiedQuad: SimpleQuad? = null

    var coverFace: EnumFacing? = null
    var faces: BooleanArray? = null

    override fun consume(quad: SimpleQuad) {
        val pixelSize = 1 / 16f
        val size = pixelSize * FacadeBakery.facadeSize

        for (vertPo in quad.vertPos) {
            if (coverFace!!.axis == EnumFacing.Axis.X) {
                vertPo.x *= size
                if (coverFace!!.axisDirection == POSITIVE)
                    vertPo.x += 1 - size
            }

            if (coverFace!!.axis == EnumFacing.Axis.Y) {
                vertPo.y *= size
                if (coverFace!!.axisDirection == POSITIVE)
                    vertPo.y += 1 - size
            }

            if (coverFace!!.axis == EnumFacing.Axis.Z) {
                vertPo.z *= size
                if (coverFace!!.axisDirection == POSITIVE)
                    vertPo.z += 1 - size
            }
        }
    }

    override fun reset() {
        coverFace = null
        faces = null
        origQuad = null
    }
}