package net.cydhra.technocracy.foundation.util.model

import com.google.common.collect.Lists
import com.google.common.collect.MultimapBuilder
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import team.chisel.ctm.client.util.Quad
import java.lang.IllegalStateException
import java.util.*


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

    constructor(format: VertexFormat) : this() {
        this.format = format
    }

    lateinit var format: VertexFormat
    var face: EnumFacing? = null
    var tintIndex: Int = -1
    var tintColor: Int = 0
    var sprite: TextureAtlasSprite? = null
    var applyDiffuseLighting = true

    val vertPos = mutableListOf<Vector3f>()
    val vertUv = mutableListOf<Vector2f>()
    val vertLight = mutableListOf<Vector2f>()
    val vertColor = mutableListOf<Vector4f>()
    val vertNormal = mutableListOf<Vector3f>()

    val data = MultimapBuilder.enumKeys(VertexFormatElement.EnumUsage::class.java).arrayListValues().build<VertexFormatElement.EnumUsage, FloatArray>()

    fun addPos(x: Float, y: Float, z: Float): SimpleQuad {
        vertPos.add(Vector3f(x, y, z))
        return this
    }

    fun addUV(u: Float, v: Float): SimpleQuad {
        vertUv.add(Vector2f(u, v))
        return this
    }

    fun addLight(block: Byte, sky: Byte): SimpleQuad {
        vertLight.add(Vector2f(block * 0x20 / 0xFFFF.toFloat(), sky * 0x20 / 0xFFFF.toFloat()))
        return this
    }

    fun addColor(r: Float, g: Float, b: Float, a: Float): SimpleQuad {
        vertColor.add(Vector4f(r, g, b, a))
        return this
    }

    fun addNormal(x: Float, y: Float, z: Float): SimpleQuad {
        vertNormal.add(Vector3f(x, y, z))
        return this
    }

    fun setFormat(format: VertexFormat): SimpleQuad {
        this.format = format
        return this
    }

    fun setFace(facing: EnumFacing): SimpleQuad {
        face = facing
        return this
    }

    fun setTint(tint: Int): SimpleQuad {
        this.tintIndex = tint
        return this
    }

    fun setTexture(textureAtlasSprite: TextureAtlasSprite): SimpleQuad {
        this.sprite = textureAtlasSprite
        return this
    }

    fun setDiffuseLight(diffuse: Boolean): SimpleQuad {
        applyDiffuseLighting = diffuse
        return this
    }

    fun normalize(min: Float, max: Float, x: Float): Float {
        return (x - min) / (max - min)
    }

    fun rotate(amount: Int): SimpleQuad {
        val uvs = arrayOfNulls<Vector2f>(4)

        val s = sprite!!

        for (i in 0..3) {
            val normalized = Vector2f(normalize(s.minU, s.maxU, vertUv[i].x), normalize(s.minV, s.maxV, vertUv[i].y))
            val uv: Vector2f
            when (amount) {
                1 -> uv = Vector2f(normalized.y, 1 - normalized.x)
                2 -> uv = Vector2f(1 - normalized.x, 1 - normalized.y)
                3 -> uv = Vector2f(1 - normalized.y, normalized.x)
                else -> uv = Vector2f(normalized.x, normalized.y)
            }
            uvs[i] = uv
        }

        for (i in uvs.indices) {
            uvs[i] = Vector2f(lerp(s.minU, s.maxU, uvs[i]!!.x), lerp(s.minV, s.maxV, uvs[i]!!.y))
        }

        this.vertUv.clear()
        for(u in uvs)
            this.vertUv.add(u!!)

        return this
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