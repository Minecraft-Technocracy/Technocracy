package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import org.lwjgl.util.vector.Vector4f


@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
object QuadUVTransformer : IQuadConsumer {
    override var origQuad: BakedQuad? = null

    var quadNum: Int = -1

    override fun reset() {
        quadNum = -1
        QuadFacadeTransformer.origQuad = null
    }

    override fun consume(quad: SimpleQuad) {
        //todo if quad is lower quad only render if slide is over 0.5 big, else just dont render
        //todo need more infos, maybe give quad enum for its slice
        val uvX = Vector4f(quad.vertUv[0].x, quad.vertUv[1].x, quad.vertUv[2].x, quad.vertUv[3].x)
        val uvY = Vector4f(quad.vertUv[0].y, quad.vertUv[1].y, quad.vertUv[2].y, quad.vertUv[3].y)

        val minUVX = getMin(uvX)
        val maxUVX = getMax(uvX)

        val distX = maxUVX - minUVX

        val minUVY = getMin(uvY)
        val maxUVY = getMax(uvY)

        val distY = maxUVY - minUVY

        val minX = 0f
        val minY = 0f
        val maxX = when (quadNum) {
            -1 -> 1f
            else -> 0.5f
        }
        val maxY = when (quadNum) {
            -1 -> 1f
            else -> 0.5f
        }

        val modX = when (quadNum) {
            1, 2 -> distX
            else -> 0f
        }
        val modY = when (quadNum) {
            3, 2 -> distY
            else -> 0f
        }

        if (quad.face != null) {
            if (quadNum == -1) {
                //modify the uv and rotation, not needed if the the quad is split up, as its handeled by ctm
                @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                when (quad.face) {
                    EnumFacing.SOUTH -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].y) / maxY
                            val changeX = (minX + quad.vertPos[i].x) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = minUVX + distX * changeX + modX
                        }
                    }
                    EnumFacing.NORTH -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].y) / maxY
                            val changeX = (minX + quad.vertPos[i].x) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = maxUVX - distX * changeX + modX
                        }
                    }
                    EnumFacing.WEST -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].y) / maxY
                            val changeX = (minX + quad.vertPos[i].z) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = minUVX + distX * changeX + modX
                        }
                    }
                    EnumFacing.EAST -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].y) / maxY
                            val changeX = (minX + quad.vertPos[i].z) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = maxUVX - distX * changeX + modX
                        }
                    }
                    EnumFacing.DOWN -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].z) / maxY
                            val changeX = (minX + quad.vertPos[i].x) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = maxUVX - distX * changeX + modX
                        }
                    }
                    EnumFacing.UP -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].z) / maxY
                            val changeX = (minX + quad.vertPos[i].x) / maxX

                            quad.vertUv[i].y = minUVY + distY * changeY + modY
                            quad.vertUv[i].x = minUVX + distX * changeX + modX
                        }
                    }
                }
            } else {
                when (quad.face!!.axis) {
                    EnumFacing.Axis.Z -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].y) / maxY
                            val changeX = (minX + quad.vertPos[i].x) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = maxUVX - distX * changeX + modX
                        }
                    }
                    EnumFacing.Axis.X -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].y) / maxY
                            val changeX = (minX + quad.vertPos[i].z) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = maxUVX - distX * changeX + modX
                        }
                    }
                    EnumFacing.Axis.Y -> {
                        for (i in 0 until quad.vertPos.size) {
                            val changeY = (minY + quad.vertPos[i].z) / maxY
                            val changeX = (minX + quad.vertPos[i].x) / maxX

                            quad.vertUv[i].y = maxUVY - distY * changeY + modY
                            quad.vertUv[i].x = maxUVX - distX * changeX + modX
                        }
                    }
                }
            }
        }
    }

    fun getMin(values: Vector4f): Float {
        return Math.min(values.x, Math.min(values.y, Math.min(values.z, values.w)))
    }

    fun getMax(values: Vector4f): Float {
        return Math.max(values.x, Math.max(values.y, Math.max(values.z, values.w)))
    }
}