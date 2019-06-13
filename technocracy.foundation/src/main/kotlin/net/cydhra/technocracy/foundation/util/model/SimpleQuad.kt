package net.cydhra.technocracy.foundation.util.model

import com.google.common.collect.Lists
import com.google.common.collect.MultimapBuilder
import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import java.lang.IllegalStateException


class SimpleQuad() {
    constructor(data: List<Vector3f>, clone: SimpleQuad) : this() {
        vertPos.addAll(data)
        face = clone.face
        tintIndex = clone.tintIndex
        sprite = clone.sprite
        applyDiffuseLighting = clone.applyDiffuseLighting
    }

    constructor(data: List<FloatArray>) : this() {
        for (d in data) {
            vertPos.add(Vector3f(d[0], d[1], d[2]))
        }
    }

    var format: VertexFormat = DefaultVertexFormats.BLOCK
    var face: EnumFacing? = null
    var tintIndex: Int = -1
    var tintColor: Int = 0
    var sprite: TextureAtlasSprite? = null
    var applyDiffuseLighting = true
    var clonePosData = false

    val vertPos = mutableListOf<Vector3f>()
    val vertUv = mutableListOf<Vector2f>()
    val vertLight = mutableListOf<Vector2f>()
    val vertColor = mutableListOf<Vector4f>()
    val vertNormal = mutableListOf<Vector3f>()

    val data = MultimapBuilder.enumKeys(VertexFormatElement.EnumUsage::class.java).arrayListValues().build<VertexFormatElement.EnumUsage, FloatArray>()

    fun reset(resetPositions: Boolean) {
        if (resetPositions)
            vertPos.clear()
        vertUv.clear()
        vertLight.clear()
        vertColor.clear()
        data.clear()
        tintColor = 0
        tintIndex = 0
        face = null
        sprite = null
        applyDiffuseLighting = true
    }

    fun cloneData(quad: BakedQuad) {
        face = quad.face
        tintIndex = quad.tintIndex
        sprite = quad.sprite
        applyDiffuseLighting = quad.shouldApplyDiffuseLighting()
        tintColor = 0
        vertColor.clear()
        data.clear()
        //vertUv.clear()

        quad.pipe(QuadCloner(this))
    }

    /**
     * subdivide code from ctm
     */
    fun subdivide(count: Int): Array<SimpleQuad> {
        if (count == 1) {
            return arrayOf(this)
        } else if (count != 4) {
            throw UnsupportedOperationException()
        }

        val rects = Lists.newArrayList<SimpleQuad>()

        val firstDivide = divide(false)
        val secondDivide = firstDivide.left.divide(true)
        rects.add(secondDivide.left)

        if (firstDivide.right != null) {
            val thirdDivide = firstDivide.right.divide(true)
            rects.add(thirdDivide.left)
            rects.add(thirdDivide.right)
        } else {
            rects.add(null)
            rects.add(null)
        }

        rects.add(secondDivide.right)

        return rects.toTypedArray()
    }

    /**
     * divide code from ctm
     */
    private fun divide(vertical: Boolean): Pair<SimpleQuad, SimpleQuad> {
        val f = 0.5f

        val firstQuad = mutableListOf<Vector3f>()
        val secondQuad = mutableListOf<Vector3f>()
        for (i in 0..3) {
            val idx = i % 4
            firstQuad.add(Vector3f(vertPos[idx]))
            secondQuad.add(Vector3f(vertPos[idx]))
        }

        val i1 = 0
        val i2 = if (vertical) 1 else 3
        val j1 = if (vertical) 3 else 1
        val j2 = 2

        firstQuad[i1].x = lerp(firstQuad[i1].x, firstQuad[i2].x, f)
        firstQuad[i1].y = lerp(firstQuad[i1].y, firstQuad[i2].y, f)
        firstQuad[i1].z = lerp(firstQuad[i1].z, firstQuad[i2].z, f)
        firstQuad[j1].x = lerp(firstQuad[j1].x, firstQuad[j2].x, f)
        firstQuad[j1].y = lerp(firstQuad[j1].y, firstQuad[j2].y, f)
        firstQuad[j1].z = lerp(firstQuad[j1].z, firstQuad[j2].z, f)

        secondQuad[i2].x = lerp(secondQuad[i1].x, secondQuad[i2].x, f)
        secondQuad[i2].y = lerp(secondQuad[i1].y, secondQuad[i2].y, f)
        secondQuad[i2].z = lerp(secondQuad[i1].z, secondQuad[i2].z, f)
        secondQuad[j2].x = lerp(secondQuad[j1].x, secondQuad[j2].x, f)
        secondQuad[j2].y = lerp(secondQuad[j1].y, secondQuad[j2].y, f)
        secondQuad[j2].z = lerp(secondQuad[j1].z, secondQuad[j2].z, f)

        val q1 = SimpleQuad(firstQuad, this)
        val q2 = SimpleQuad(secondQuad, this)
        return Pair.of(q1, q2)
    }

    fun lerp(a: Float, b: Float, f: Float): Float {
        return a * (1 - f) + b * f
    }

    fun recalculateUV(quadNum: Int = -1) {
        //todo if quad is lower quad only render if slide is over 0.5 big, else just dont render
        //todo need more infos, maybe give quad enum for its slice
        val uvX = Vector4f(vertUv[0].x, vertUv[1].x, vertUv[2].x, vertUv[3].x)
        val uvY = Vector4f(vertUv[0].y, vertUv[1].y, vertUv[2].y, vertUv[3].y)

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

        if (face != null) {
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (face!!.axis) {
                EnumFacing.Axis.Z -> {
                    for (i in 0 until vertPos.size) {
                        val changeY = (minY + vertPos[i].y) / maxY
                        val changeX = (minX + vertPos[i].x) / maxX

                        vertUv[i].y = maxUVY - distY * changeY + modY
                        vertUv[i].x = maxUVX - distX * changeX + modX
                    }
                }
                EnumFacing.Axis.X -> {
                    for (i in 0 until vertPos.size) {
                        val changeY = (minY + vertPos[i].y) / maxY
                        val changeX = (minX + vertPos[i].z) / maxX

                        vertUv[i].y = maxUVY - distY * changeY + modY
                        vertUv[i].x = maxUVX - distX * changeX + modX
                    }
                }
                EnumFacing.Axis.Y -> {
                    for (i in 0 until vertPos.size) {
                        val changeY = (minY + vertPos[i].z) / maxY
                        val changeX = (minX + vertPos[i].x) / maxX

                        vertUv[i].y = maxUVY - distY * changeY + modY
                        vertUv[i].x = maxUVX - distX * changeX + modX
                    }
                }
            }
        }
    }

    fun recalculateVertexPoses(coverFace: EnumFacing, faces: BooleanArray) {
        val pixelSize = 1 / 16f
        val height = pixelSize * FacadeBakery.facadeSize

        val minX = getMin(Vector4f(vertPos[0].x,vertPos[1].x,vertPos[2].x,vertPos[3].x))
        val minY = getMin(Vector4f(vertPos[0].y,vertPos[1].y,vertPos[2].y,vertPos[3].y))
        val minZ = getMin(Vector4f(vertPos[0].z,vertPos[1].z,vertPos[2].z,vertPos[3].z))
        val maxX = getMax(Vector4f(vertPos[0].x,vertPos[1].x,vertPos[2].x,vertPos[3].x))
        val maxY = getMax(Vector4f(vertPos[0].y,vertPos[1].y,vertPos[2].y,vertPos[3].y))
        val maxZ = getMax(Vector4f(vertPos[0].z,vertPos[1].z,vertPos[2].z,vertPos[3].z))

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

        for(vertices in 0 until 4) {
            if (this.face == EnumFacing.NORTH) {
                when (vertices) {
                    1 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    2 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    3 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    0 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (this.face == EnumFacing.SOUTH) {
                when (vertices) {
                    1 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            if (this.face == EnumFacing.WEST) {
                when (vertices) {
                    1 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    3 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    0 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            if (this.face == EnumFacing.EAST) {
                when (vertices) {
                    1 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    2 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (this.face == EnumFacing.UP) {
                when (vertices) {
                    0 -> {//2
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    1 -> {//3
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {//0
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {//1
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                        vertPos[vertices].y = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                }
            }

            if (this.face == EnumFacing.DOWN) {
                when (vertices) {
                    1 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                    2 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    3 -> {
                        vertPos[vertices].x = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                    }
                    0 -> {
                        vertPos[vertices].x = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                        vertPos[vertices].y = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                        vertPos[vertices].z = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                    }
                }
            }

            //clamp

            vertPos[vertices].x = MathHelper.clamp(vertPos[vertices].x, 0f, 1f)
            vertPos[vertices].y = MathHelper.clamp(vertPos[vertices].y, 0f, 1f)
            vertPos[vertices].z = MathHelper.clamp(vertPos[vertices].z, 0f, 1f)
        }
    }

    fun getMin(values: Vector4f): Float {
        return Math.min(values.x, Math.min(values.y, Math.min(values.z, values.w)))
    }

    fun getMax(values: Vector4f): Float {
        return Math.max(values.x, Math.max(values.y, Math.max(values.z, values.w)))
    }

    fun bake(): BakedQuad {
        if (face == null || sprite == null) {
            throw IllegalStateException("Quad data not consistent")
        }

        val builder = UnpackedBakedQuad.Builder(format)
        builder.setQuadOrientation(face!!)
        builder.setQuadTint(tintIndex)
        builder.setApplyDiffuseLighting(applyDiffuseLighting)
        builder.setTexture(sprite!!)

        for (v in 0..3) {
            for (i in 0 until format.elementCount) {
                val ele = format.getElement(i)
                when (ele.usage) {
                    VertexFormatElement.EnumUsage.POSITION -> {
                        val pos = vertPos[v]
                        builder.put(i, pos.x, pos.y, pos.z, 0f)
                    }
                    VertexFormatElement.EnumUsage.UV -> {
                        if (ele.index == 1) {
                            if (vertLight.size > v) {
                                val light = vertLight[v]
                                builder.put(i, light.x, light.y)
                            } else {
                                builder.put(i, *data.get(ele.usage)[v])
                            }
                        } else if (ele.index == 0) {
                            val uv = vertUv[v]
                            builder.put(i, uv.x, uv.y, 0f, 0f)
                        }
                    }
                    VertexFormatElement.EnumUsage.COLOR -> {
                        val color = vertColor[v]

                        if (tintIndex != -1) {
                            val r = (tintColor shr 0x10 and 0xFF).toFloat() / 255f
                            val g = (tintColor shr 0x08 and 0xFF).toFloat() / 255f
                            val b = (tintColor and 0xFF).toFloat() / 255f

                            builder.put(i, color.x * r, color.y * g, color.z * b, color.w)
                        } else {
                            builder.put(i, color.x, color.y, color.z, color.w)
                        }
                    }
                    VertexFormatElement.EnumUsage.NORMAL -> {
                        if (vertNormal.size > v) {
                            val normal = vertNormal[v]
                            builder.put(i, normal.x, normal.y, normal.z)
                        } else {
                            builder.put(i, *data.get(ele.usage)[v])
                        }
                    }
                    else -> {
                        builder.put(i, *data.get(ele.usage)[v])
                    }
                }
            }
        }
        return builder.build()
    }
}