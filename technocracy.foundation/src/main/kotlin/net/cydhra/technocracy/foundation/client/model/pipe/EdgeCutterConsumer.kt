package net.cydhra.technocracy.foundation.client.model.pipe

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.model.pipeline.IVertexConsumer
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import team.chisel.ctm.client.util.Quad


class EdgeCutterConsumer(var coverFace: EnumFacing, var faces: BooleanArray, var parent: UnpackedBakedQuad.Builder) : IVertexConsumer {
    override fun getVertexFormat(): VertexFormat {
        return parent.vertexFormat
    }

    //var cache = arrayListOf<FloatArray>()

    override fun put(element: Int, vararg dataIn: Float) {
        //val data = FloatArray(dataIn.size - 1)
        //System.arraycopy(dataIn, 0, data, 0, dataIn.size - 1)

        val format = parent.vertexFormat
        val usage = format.getElement(element)
        if (usage.usage == VertexFormatElement.EnumUsage.POSITION) {

            //cache.add(dataIn)

            poses++
            parent.put(element, *modify(dataIn.clone()))
        } else {
            parent.put(element, *dataIn)
        }

        /*if(cache.size == 4) {
            poses = 0
            var minX = 1f
            var maxX = 0f
            var minY = 1f
            var maxY = 0f
            var minZ = 1f
            var maxZ = 0f
            for(dat in cache) {

                val x = dat[0]
                val y = dat[1]
                val z = dat[2]

                minX = Math.min(x, minX)
                maxX = Math.max(x, maxX)
                minY = Math.min(y, minY)
                maxY = Math.max(y, maxY)
                minZ = Math.min(z, minZ)
                maxZ = Math.max(z, maxZ)
            }

            println("$facing $index ->  $maxX f, $minX f, $maxY f, $minY f, $maxZ f, $minZ f")

            cache.clear()
        }*/

        elements++
        if (elements == format.elementCount) {

            vertices++
            elements = 0
        }
    }

    private var tint = -1
    private var diffuse = true
    private var vertices = 0
    private var elements = 0
    private var poses = 0

    var maxIndex: Int = 1
    var index: Int = 0

    var tex: TextureAtlasSprite? = null
    var facing: EnumFacing? = null

    override fun setQuadTint(tint: Int) {
        this.tint = tint
    }

    fun modify(data: FloatArray): FloatArray {

        if (facing == null)
            return data

        val pixelSize = 1 / 16f

        val size = 1

        val height = pixelSize * size

        //Down 0, Up 1, North 2, South 3, West 4, East 5
        //val faces = booleanArrayOf(false, true, false, true, false, true)

        var minX = 0.0f
        var minY = 0.0f
        var minZ = 0.0f
        var maxX = 1.0f
        var maxY = 1.0f
        var maxZ = 1.0f

        if(maxIndex != 1) {
            when (facing) {
                EnumFacing.UP -> {
                    val arrays = mutableMapOf<Int, Array<Float>>()
                    arrays[0] = arrayOf(1f, 0.5f, 1f, 0.5f)//Q1
                    arrays[1] = arrayOf(0.5f, 0f, 1f, 0.5f)//Q2
                    arrays[2] = arrayOf(0.5f, 0f, 0.5f, 0f)//Q3
                    arrays[3] = arrayOf(1f, 0.5f, 0.5f, 0f)//Q4

                    val a = arrays[index]!!

                    maxX = a[0]
                    minX = a[1]
                    maxZ = a[2]
                    minZ = a[3]
                }
                EnumFacing.DOWN -> {
                    val arrays = mutableMapOf<Int, Array<Float>>()
                    arrays[0] = arrayOf(1.0f, 0.5f, 0.5f, 0.0f)//Q1
                    arrays[1] = arrayOf(0.5f, 0.0f, 0.5f, 0.0f)//Q2
                    arrays[2] = arrayOf(0.5f, 0.0f, 1.0f, 0.5f)//Q3
                    arrays[3] = arrayOf(1.0f, 0.5f, 1.0f, 0.5f)//Q4

                    val a = arrays[index]!!

                    maxX = a[0]
                    minX = a[1]
                    maxZ = a[2]
                    minZ = a[3]
                }
                EnumFacing.SOUTH -> {
                    val arrays = mutableMapOf<Int, Array<Float>>()
                    arrays[0] = arrayOf(1.0f, 0.5f, 0.5f, 0.0f)//Q1
                    arrays[1] = arrayOf(0.5f, 0.0f, 0.5f, 0.0f)//Q2
                    arrays[2] = arrayOf(0.5f, 0.0f, 1.0f, 0.5f)//Q3
                    arrays[3] = arrayOf(1.0f, 0.5f, 1.0f, 0.5f)//Q4

                    val a = arrays[index]!!

                    maxX = a[0]
                    minX = a[1]
                    maxY = a[2]
                    minY = a[3]
                }
                EnumFacing.NORTH -> {
                    val arrays = mutableMapOf<Int, Array<Float>>()
                    arrays[0] = arrayOf(0.5f, 0.0f, 0.5f, 0.0f)//Q1
                    arrays[1] = arrayOf(1.0f, 0.5f, 0.5f, 0.0f)//Q2
                    arrays[2] = arrayOf(1.0f, 0.5f, 1.0f, 0.5f)//Q3
                    arrays[3] = arrayOf(0.5f, 0.0f, 1.0f, 0.5f)//Q4

                    val a = arrays[index]!!

                    maxX = a[0]
                    minX = a[1]
                    maxY = a[2]
                    minY = a[3]
                }
                EnumFacing.WEST -> {
                    val arrays = mutableMapOf<Int, Array<Float>>()
                    arrays[0] = arrayOf(0.5f, 0.0f, 1.0f, 0.5f)//Q1
                    arrays[1] = arrayOf(0.5f, 0.0f, 0.5f, 0.0f)//Q2
                    arrays[2] = arrayOf(1.0f, 0.5f, 0.5f, 0.0f)//Q3
                    arrays[3] = arrayOf(1.0f, 0.5f, 1.0f, 0.5f)//Q4

                    val a = arrays[index]!!

                    maxY = a[0]
                    minY = a[1]
                    maxZ = a[2]
                    minZ = a[3]
                }
                EnumFacing.EAST -> {
                    val arrays = mutableMapOf<Int, Array<Float>>()
                    arrays[0] = arrayOf(0.5f, 0.0f, 0.5f, 0.0f)//Q1
                    arrays[1] = arrayOf(0.5f, 0.0f, 1.0f, 0.5f)//Q2
                    arrays[2] = arrayOf(1.0f, 0.5f, 1.0f, 0.5f)//Q3
                    arrays[3] = arrayOf(1.0f, 0.5f, 0.5f, 0.0f)//Q4

                    val a = arrays[index]!!

                    maxY = a[0]
                    minY = a[1]
                    maxZ = a[2]
                    minZ = a[3]
                }
            }
        }

        val up = faces[EnumFacing.UP.ordinal]
        val down = faces[EnumFacing.DOWN.ordinal]
        val north = faces[EnumFacing.NORTH.ordinal]
        val south = faces[EnumFacing.SOUTH.ordinal]
        val east = faces[EnumFacing.EAST.ordinal]
        val west = faces[EnumFacing.WEST.ordinal]

        val yWidth = when (this.coverFace) {
            EnumFacing.UP -> -(1 - height)
            EnumFacing.DOWN -> 1 - height
            else -> 0.0f
        }

        val xWidth = when (this.coverFace) {
            EnumFacing.EAST -> -(1 - height)
            EnumFacing.WEST -> 1 - height
            else -> 0.0f
        }

        val zWidth = when (this.coverFace) {
            EnumFacing.SOUTH -> -(1 - height)
            EnumFacing.NORTH -> 1 - height
            else -> 0.0f
        }

        if (facing == EnumFacing.NORTH) {
            when (vertices) {
                1 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                2 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                3 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                0 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.SOUTH) {
            when (vertices) {
                1 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                0 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.WEST) {
            when (vertices) {
                1 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                3 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                0 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.EAST) {
            when (vertices) {
                1 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                2 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                0 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.UP) {
            when (vertices) {
                0 -> {//2
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (maxIndex == 1 && north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                1 -> {//3
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (maxIndex == 1 && south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {//0
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (maxIndex == 1 && south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {//1
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                    data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (maxIndex == 1 &&north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                }
            }
        }

        if (facing == EnumFacing.DOWN) {
            when (vertices) {
                1 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
                2 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                3 -> {
                    data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                }
                0 -> {
                    data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                    data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                    data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                }
            }
        }

        //clamp

        data[0] = MathHelper.clamp(data[0], 0f, 1f)
        data[1] = MathHelper.clamp(data[1], 0f, 1f)
        data[2] = MathHelper.clamp(data[2], 0f, 1f)
        return data
    }

    fun build(): BakedQuad {
        return parent.build()
    }

    override fun setQuadOrientation(orientation: EnumFacing) {
        parent.setQuadOrientation(orientation)
    }

    override fun setTexture(texture: TextureAtlasSprite) {
        this.tex = texture
        parent.setTexture(texture)
    }

    override fun setApplyDiffuseLighting(diffuse: Boolean) {
        this.diffuse = diffuse
    }
}