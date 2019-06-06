package net.cydhra.technocracy.foundation.client.model.pipe

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad


object FacadeBakery {
    fun getFacadeQuads(coverFace: EnumFacing, state: IBlockState, pos: BlockPos): MutableList<BakedQuad> {

        val quads = mutableListOf<BakedQuad>()
        val dispatcher = Minecraft.getMinecraft().blockRendererDispatcher
        val coverModel = dispatcher.getModelForState(state.getActualState(Minecraft.getMinecraft().world, pos))


        val pixelSize = 1 / 16f
        val size = 1
        val height = pixelSize * size

        val boundingBox = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

        //Down 0, Up 1, North 2, South 3, West 4, East 5
        //TODO use real faces
        val faces = booleanArrayOf(true, true, true, true, true, true)

        val minX = boundingBox.minX.toFloat()
        val minY = boundingBox.minY.toFloat()
        val minZ = boundingBox.minZ.toFloat()
        val maxX = boundingBox.maxX.toFloat()
        val maxY = boundingBox.maxY.toFloat()
        val maxZ = boundingBox.maxZ.toFloat()

        val up = faces[EnumFacing.UP.ordinal]
        val down = faces[EnumFacing.DOWN.ordinal]
        val north = faces[EnumFacing.NORTH.ordinal]
        val south = faces[EnumFacing.SOUTH.ordinal]
        val east = faces[EnumFacing.EAST.ordinal]
        val west = faces[EnumFacing.WEST.ordinal]

        for (quadFace in EnumFacing.values()) {

            val yWidth = when (coverFace) {
                EnumFacing.UP -> -(1 - height)
                EnumFacing.DOWN -> 1 - height
                else -> 0.0f
            }

            val xWidth = when (coverFace) {
                EnumFacing.EAST -> -(1 - height)
                EnumFacing.WEST -> 1 - height
                else -> 0.0f
            }

            val zWidth = when (coverFace) {
                EnumFacing.SOUTH -> -(1 - height)
                EnumFacing.NORTH -> 1 - height
                else -> 0.0f
            }

            val data = FloatArray(3)

            val quad = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad.setTexture(coverModel.getQuads(state, quadFace, 0).first().sprite)

            if (quadFace == EnumFacing.NORTH) {
                for (el in 0 until 4) {
                    when (el) {
                        0 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        1 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        2 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = minZ - zWidth + if (south && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        3 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = minZ - zWidth + if (south && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                    }

                    data[0] = MathHelper.clamp(data[0], 0f, 1f)
                    data[1] = MathHelper.clamp(data[1], 0f, 1f)
                    data[2] = MathHelper.clamp(data[2], 0f, 1f)

                    quad.put(0, data[0], data[1], data[2], 1f)
                    quad.put(1, data[0], data[1], data[2], 1f)
                    quad.put(2, 1f, 1f, 1f, 1f)
                }
            }

            if (quadFace == EnumFacing.SOUTH) {
                for (el in 0 until 4) {
                    when (el) {
                        0 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        1 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        2 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = maxZ - zWidth - if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        3 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = maxZ - zWidth - if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                    }
                    data[0] = MathHelper.clamp(data[0], 0f, 1f)
                    data[1] = MathHelper.clamp(data[1], 0f, 1f)
                    data[2] = MathHelper.clamp(data[2], 0f, 1f)

                    quad.put(0, data[0], data[1], data[2], 1f)
                    quad.put(1, data[0], data[1], data[2], 1f)
                    quad.put(2, 1f, 1f, 1f, 1f)
                }
            }

            if (quadFace == EnumFacing.WEST) {
                for (el in 0 until 4) {
                    when (el) {
                        0 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = minZ - zWidth + if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        1 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        2 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = maxZ - zWidth - if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        3 -> {
                            data[0] = minX - xWidth + +if (west && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                    }
                    data[0] = MathHelper.clamp(data[0], 0f, 1f)
                    data[1] = MathHelper.clamp(data[1], 0f, 1f)
                    data[2] = MathHelper.clamp(data[2], 0f, 1f)

                    quad.put(0, data[0], data[1], data[2], 1f)
                    quad.put(1, data[0], data[1], data[2], 1f)
                    quad.put(2, 1f, 1f, 1f, 1f)
                }
            }

            if (quadFace == EnumFacing.EAST) {
                for (el in 0 until 4) {
                    when (el) {
                        0 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        1 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = minZ - zWidth + if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        2 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = minX - zWidth + if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        3 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = maxX - zWidth - if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                    }
                    data[0] = MathHelper.clamp(data[0], 0f, 1f)
                    data[1] = MathHelper.clamp(data[1], 0f, 1f)
                    data[2] = MathHelper.clamp(data[2], 0f, 1f)

                    quad.put(0, data[0], data[1], data[2], 1f)
                    quad.put(1, data[0], data[1], data[2], 1f)
                    quad.put(2, 1f, 1f, 1f, 1f)
                }
            }

            if (quadFace == EnumFacing.UP) {
                for (el in 0 until 4) {
                    when (el) {
                        0 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        1 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        2 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        3 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN) height else 0.0f
                            data[1] = maxY - yWidth - if (up && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                    }
                    data[0] = MathHelper.clamp(data[0], 0f, 1f)
                    data[1] = MathHelper.clamp(data[1], 0f, 1f)
                    data[2] = MathHelper.clamp(data[2], 0f, 1f)

                    quad.put(0, data[0], data[1], data[2], 1f)
                    quad.put(1, data[0], data[1], data[2], 1f)
                    quad.put(2, 1f, 1f, 1f, 1f)
                }
            }

            if (quadFace == EnumFacing.DOWN) {
                for (el in 0 until 4) {
                    when (el) {
                        0 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                        1 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = minZ - zWidth + if (north && coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        2 -> {
                            data[0] = maxX - xWidth - if (east && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST) height else 0.0f
                        }
                        3 -> {
                            data[0] = minX - xWidth + if (west && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP) height else 0.0f
                            data[1] = minY - yWidth + if (down && coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST) height else 0.0f
                        }
                    }
                    data[0] = MathHelper.clamp(data[0], 0f, 1f)
                    data[1] = MathHelper.clamp(data[1], 0f, 1f)
                    data[2] = MathHelper.clamp(data[2], 0f, 1f)

                    quad.put(0, data[0], data[1], data[2], 1f)
                    quad.put(1, data[0], data[1], data[2], 1f)
                    quad.put(2, 1f, 1f, 1f, 1f)
                }
            }
            quads.add(quad.build())
        }
        return quads
    }

}