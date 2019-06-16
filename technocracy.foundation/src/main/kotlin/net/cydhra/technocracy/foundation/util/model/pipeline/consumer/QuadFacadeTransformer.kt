package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import org.lwjgl.util.vector.Vector4f


object QuadFacadeTransformer : IQuadConsumer {
    override var origQuad: BakedQuad? = null

    var coverFace: EnumFacing? = null
    var faces: BooleanArray? = null

    override fun reset() {
        coverFace = null
        faces = null
        origQuad = null
    }

    override fun consume(quad: SimpleQuad) {
        if (faces == null || coverFace == null)
            return

        val faces = faces!!

        val pixelSize = 1 / 16f
        val height = pixelSize * FacadeBakery.facadeSize

        val minX = getMin(Vector4f(quad.vertPos[0].x, quad.vertPos[1].x, quad.vertPos[2].x, quad.vertPos[3].x))
        val minY = getMin(Vector4f(quad.vertPos[0].y, quad.vertPos[1].y, quad.vertPos[2].y, quad.vertPos[3].y))
        val minZ = getMin(Vector4f(quad.vertPos[0].z, quad.vertPos[1].z, quad.vertPos[2].z, quad.vertPos[3].z))
        val maxX = getMax(Vector4f(quad.vertPos[0].x, quad.vertPos[1].x, quad.vertPos[2].x, quad.vertPos[3].x))
        val maxY = getMax(Vector4f(quad.vertPos[0].y, quad.vertPos[1].y, quad.vertPos[2].y, quad.vertPos[3].y))
        val maxZ = getMax(Vector4f(quad.vertPos[0].z, quad.vertPos[1].z, quad.vertPos[2].z, quad.vertPos[3].z))

        val yWidth = when (coverFace) {
            EnumFacing.UP -> -(1f - height)
            EnumFacing.DOWN -> 1f - height
            else -> 0f
        }

        val xWidth = when (coverFace) {
            EnumFacing.EAST -> -(1f - height)
            EnumFacing.WEST -> 1f - height
            else -> 0f
        }

        val zWidth = when (coverFace) {
            EnumFacing.SOUTH -> -(1f - height)
            EnumFacing.NORTH -> 1f - height
            else -> 0f
        }

        val up = faces[EnumFacing.UP.ordinal]
        val down = faces[EnumFacing.DOWN.ordinal]
        val north = faces[EnumFacing.NORTH.ordinal]
        val south = faces[EnumFacing.SOUTH.ordinal]
        val east = faces[EnumFacing.EAST.ordinal]
        val west = faces[EnumFacing.WEST.ordinal]

        for (vertices in 0 until 4) {
            if (quad.face == EnumFacing.NORTH) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.SOUTH) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.WEST) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.EAST) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.UP) {
                when (vertices) {
                    0 -> {//2
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    1 -> {//3
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {//0
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {//1
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.DOWN) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            //clamp

            quad.vertPos[vertices].x = MathHelper.clamp(quad.vertPos[vertices].x, 0f, 1f)
            quad.vertPos[vertices].y = MathHelper.clamp(quad.vertPos[vertices].y, 0f, 1f)
            quad.vertPos[vertices].z = MathHelper.clamp(quad.vertPos[vertices].z, 0f, 1f)
        }
    }

    private fun getMin(values: Vector4f): Float {
        return Math.min(values.x, Math.min(values.y, Math.min(values.z, values.w)))
    }

    private fun getMax(values: Vector4f): Float {
        return Math.max(values.x, Math.max(values.y, Math.max(values.z, values.w)))
    }
}