package net.cydhra.technocracy.foundation.util

import com.google.common.collect.Lists
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.IVertexConsumer
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import java.lang.IllegalStateException

class SimpleQuad() {
    constructor(data: List<Vector3f>, clone: SimpleQuad): this() {
        vertPos.addAll(data)
        face = clone.face
        tintIndex = clone.tintIndex
        sprite = clone.sprite
        applyDiffuseLighting = clone.applyDiffuseLighting
    }

    constructor(data: List<FloatArray>): this() {
        for(d in data) {
            vertPos.add(Vector3f(d[0], d[1], d[2]))
        }
    }

    val format: VertexFormat = DefaultVertexFormats.BLOCK
    var face: EnumFacing? = null
    var tintIndex: Int = -1
    var sprite: TextureAtlasSprite? = null
    var applyDiffuseLighting: Boolean = true

    val vertPos = mutableListOf<Vector3f>()
    val vertUv = mutableListOf<Vector2f>()

    val otherData = mutableMapOf<VertexFormatElement.EnumUsage, HashMap<Int, Vector4f>>()

    fun cloneData(quad: BakedQuad) {
        face = quad.face
        tintIndex = quad.tintIndex
        sprite = quad.sprite
        applyDiffuseLighting = quad.shouldApplyDiffuseLighting()

        quad.pipe(DataCloner(this))
    }

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

    private fun divide(vertical: Boolean): Pair<SimpleQuad, SimpleQuad> {
        val f = 0.5f

        val firstQuad = mutableListOf<Vector3f>()
        val secondQuad = mutableListOf<Vector3f>()
        for (i in 0..3) {
            val idx = i % 4
            firstQuad[i] = Vector3f(vertPos[idx])
            secondQuad[i] = Vector3f(vertPos[idx])
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
        builder.setQuadOrientation(face)
        builder.setQuadTint(tintIndex)
        builder.setApplyDiffuseLighting(applyDiffuseLighting)
        builder.setTexture(sprite)

        for (v in 0..3) {
            for (i in 0 until format.elementCount) {
                val ele = format.getElement(i)
                when (ele.usage) {
                    VertexFormatElement.EnumUsage.UV -> {
                        val uv = vertUv[v]
                        builder.put(i, uv.x, uv.y, 0f, 1f)
                    }
                    VertexFormatElement.EnumUsage.POSITION -> {
                        val p = vertPos[v]
                        builder.put(i, p.x, p.y, p.z, 1f)
                    }
                    else -> {
                    }
                }
            }
        }

        otherData.forEach { enum, map ->
            map.forEach { index, data ->
                builder.put(index, data.x, data.y, data.z, data.w)
            }
        }

        return builder.build()
    }

    class DataCloner(val quad: SimpleQuad) : IVertexConsumer {
        override fun getVertexFormat(): VertexFormat {
            return quad.format
        }

        override fun put(element: Int, vararg data: Float) {
            val usage = vertexFormat.getElement(element)
            if (usage.usage == VertexFormatElement.EnumUsage.UV) {
                quad.vertUv[element] = Vector2f(data[0], data[1])
            } else {
                quad.otherData.getOrPut(usage.usage) {
                    HashMap()
                }[element] = Vector4f(data[0], data[1], data[2], data[3])
            }
        }

        override fun setQuadOrientation(orientation: EnumFacing) {
        }

        override fun setTexture(texture: TextureAtlasSprite) {
        }

        override fun setApplyDiffuseLighting(diffuse: Boolean) {
        }

        override fun setQuadTint(tint: Int) {
        }

    }
}