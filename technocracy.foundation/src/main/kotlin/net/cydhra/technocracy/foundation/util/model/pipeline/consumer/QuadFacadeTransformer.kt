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
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.SOUTH) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.WEST) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.EAST) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.UP) {
                when (vertices) {
                    0 -> {//2
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    1 -> {//3
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {//0
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {//1
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        quad.vertPos[vertices].y -= if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (quad.face == EnumFacing.DOWN) {
                when (vertices) {
                    1 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z += if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        quad.vertPos[vertices].x -= if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        quad.vertPos[vertices].x += if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        quad.vertPos[vertices].y += if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        quad.vertPos[vertices].z -= if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }
        }
    }
}