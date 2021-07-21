package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.AxisDirection
import net.minecraft.util.EnumFacing.AxisDirection.NEGATIVE
import net.minecraft.util.EnumFacing.AxisDirection.POSITIVE
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.util.vector.Vector3f
import kotlin.math.abs


class QuadCornerKicker(var mySide: Int, var facadeMask: Int, var box: AxisAlignedBB) : IQuadConsumer {

    override fun reset() {
    }

    companion object {
        var horizonals = arrayOf(
            intArrayOf(2, 3, 4, 5),
            intArrayOf(2, 3, 4, 5),
            intArrayOf(0, 1, 4, 5),
            intArrayOf(0, 1, 4, 5),
            intArrayOf(0, 1, 2, 3),
            intArrayOf(0, 1, 2, 3)
        )
    }

    fun epsComp(a: Float, b: Float): Boolean {
        return if (a == b) {
            true
        } else {
            abs(a - b) < 0.01
        }
    }

    val pixelSize = 1 / 16f
    val thickness = pixelSize * FacadeBakery.facadeSize

    override fun consume(quad: SimpleQuad) {
        val side: Int = quad.face.ordinal

        if (side != mySide) {
            val vertPos = mutableListOf<Vector3f>()
            for (v in quad.vertPos)
                vertPos.add(Vector3f(v))

            val sideaxis = EnumFacing.VALUES[mySide].axis
            for (hoz in horizonals[mySide]) {
                if (side != hoz xor 1) {
                    if (facadeMask and (1 shl hoz) != 0) {
                        val corner = Corner.fromSides(mySide xor 1, side, hoz)

                        val hozaxis = EnumFacing.VALUES[hoz].axis

                        val ignorez =
                            (hozaxis == EnumFacing.Axis.Y && sideaxis == EnumFacing.Axis.X) || (hozaxis == EnumFacing.Axis.X && sideaxis == EnumFacing.Axis.Y)
                        val ignorex =
                            (hozaxis == EnumFacing.Axis.Y && sideaxis == EnumFacing.Axis.Z) || (hozaxis == EnumFacing.Axis.Z && sideaxis == EnumFacing.Axis.Y)
                        val ignorey = hozaxis != EnumFacing.Axis.Y && sideaxis != EnumFacing.Axis.Y

                        for ((index, vertex) in quad.vertPos.withIndex()) {
                            val tmp = vertPos[index]
                            if ((ignorex || epsComp(tmp.getX(), corner.pX(box))) && (ignorey || epsComp(
                                    tmp.getY(),
                                    corner.pY(box)
                                )) && (ignorez || epsComp(tmp.getZ(), corner.pZ(box)))
                            ) {
                                val vec = EnumFacing.VALUES[hoz].directionVec
                                vertex.x -= vec.x * this.thickness
                                vertex.y -= vec.y * this.thickness
                                vertex.z -= vec.z * this.thickness
                            }
                        }
                    }
                }
            }
        }
    }


    override lateinit var origQuad: BakedQuad
    override lateinit var unmodifiedQuad: SimpleQuad

    enum class Corner(
        private val xAxis: AxisDirection,
        private val yAxis: AxisDirection,
        private val zAxis: AxisDirection
    ) {
        MIN_X_MIN_Y_MIN_Z(NEGATIVE, NEGATIVE, NEGATIVE), MIN_X_MIN_Y_MAX_Z(
            NEGATIVE,
            NEGATIVE,
            POSITIVE
        ),
        MIN_X_MAX_Y_MIN_Z(NEGATIVE, POSITIVE, NEGATIVE), MIN_X_MAX_Y_MAX_Z(
            NEGATIVE,
            POSITIVE,
            POSITIVE
        ),
        MAX_X_MIN_Y_MIN_Z(POSITIVE, NEGATIVE, NEGATIVE), MAX_X_MIN_Y_MAX_Z(
            POSITIVE,
            NEGATIVE,
            POSITIVE
        ),
        MAX_X_MAX_Y_MIN_Z(POSITIVE, POSITIVE, NEGATIVE), MAX_X_MAX_Y_MAX_Z(POSITIVE, POSITIVE, POSITIVE);

        fun pX(box: AxisAlignedBB): Float {
            return (if (xAxis == NEGATIVE) box.minX else box.maxX).toFloat()
        }

        fun pY(box: AxisAlignedBB): Float {
            return (if (yAxis == NEGATIVE) box.minY else box.maxY).toFloat()
        }

        fun pZ(box: AxisAlignedBB): Float {
            return (if (zAxis == NEGATIVE) box.minZ else box.maxZ).toFloat()
        }

        companion object {
            private val sideMask = intArrayOf(0, 2, 0, 1, 0, 4)

            /**
             * Used to find what corner is at the 3 sides.
             * This method assumes you pass in the X axis side, Y axis side, and Z axis side,
             * it will NOT complain about an invalid side, you will just get garbage data.
             * This method also does not care what order the 3 axes are in.
             *
             * @param sideA Side one.
             * @param sideB Side two.
             * @param sideC Side three.
             *
             * @return The corner at the 3 sides.
             */
            fun fromSides(sideA: Int, sideB: Int, sideC: Int): Corner {
                // <3 Chicken-Bones.
                return values()[sideMask[sideA] or sideMask[sideB] or sideMask[sideC]]
            }
        }
    }

}