package net.cydhra.technocracy.foundation.util.model.pipeline.consumer

import net.cydhra.technocracy.foundation.util.model.InterpHelper
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.IQuadConsumer
import net.minecraft.client.renderer.block.model.BakedQuad
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f


@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
object QuadUVTransformer : IQuadConsumer {
    override lateinit var  origQuad: BakedQuad
    override lateinit var  unmodifiedQuad: SimpleQuad

    override fun reset() {
    }

    /**
     * Gets the 2d X coord for the given axis.
     *
     * @param s The axis. side >> 1
     *
     * @return The x coord.
     */
    fun Vector3f.dx(s: Int): Float {
        return if (s <= 1) {
            this.x
        } else {
            this.z
        }
    }

    /**
     * Gets the 2d Y coord for the given axis.
     *
     * @param s The axis. side >> 1
     *
     * @return The y coord.
     */
    fun Vector3f.dy(s: Int): Float {
        return if (s > 0) {
            this.y
        } else {
            this.z
        }
    }

    fun Vector4f.get(index: Int): Float {
        return when (index) {
            0 -> this.x
            1 -> this.y
            2 -> this.z
            3 -> this.w
            else -> 0f
        }
    }

    fun Vector4f.set(index: Int, float: Float) {
        when (index) {
            0 -> this.x = float
            1 -> this.y = float
            2 -> this.z = float
            3 -> this.w = float
        }
    }

    fun Vector3f.get(index: Int): Float {
        return when (index) {
            0 -> this.x
            1 -> this.y
            2 -> this.z
            else -> 0f
        }
    }

    fun Vector3f.set(index: Int, float: Float) {
        when (index) {
            0 -> this.x = float
            1 -> this.y = float
            2 -> this.z = float
        }
    }

    fun Vector2f.get(index: Int): Float {
        return when (index) {
            0 -> this.x
            1 -> this.y
            else -> 0f
        }
    }

    fun Vector2f.set(index: Int, float: Float) {
        when (index) {
            0 -> this.x = float
            1 -> this.y = float
        }
    }

    /**
     * Interpolates the new color values using the unmodified quad as reference.
     *
     * @param color The color to interpolate
     * @param interpHelper The InterpHelper to use
     * @param others The unmodified quad
     *
     * @return The color Vector
     */
    fun interpColorFrom(color: Vector4f, others: SimpleQuad): Vector4f {
        for (e in 0..3) {
            val p1: Float = others.vertColor[0].get(e)
            val p2: Float = others.vertColor[1].get(e)
            val p3: Float = others.vertColor[2].get(e)
            val p4: Float = others.vertColor[3].get(e)
            // Only interpolate if colors are different.
            if (p1 != p2 || p2 != p3 || p3 != p4) {
                color.set(e, InterpHelper.interpolate(p1, p2, p3, p4))
            }
        }
        return color
    }

    /**
     * Interpolates the new UV values using the unmodified quad as reference.
     *
     * @param uv The uv Vector
     * @param interpHelper The InterpHelper to use
     * @param others The unmodified quad
     *
     * @return The uv Vecotor
     */
    fun interpUVFrom(uv: Vector2f, others: SimpleQuad): Vector2f {
        for (e in 0..1) {
            val p1: Float = others.vertUv[0].get(e)
            val p2: Float = others.vertUv[1].get(e)
            val p3: Float = others.vertUv[2].get(e)
            val p4: Float = others.vertUv[3].get(e)
            if (p1 != p2 || p2 != p3 || p3 != p4) {
                uv.set(e, InterpHelper.interpolate(p1, p2, p3, p4))
            }
        }
        return uv
    }

    /**
     * Interpolates the new LightMap values using the unmodified quad as reference.
     *
     * @param lightMap The lightMap Vector.
     * @param interpHelper The InterpHelper to use.
     * @param others The unmodified quad.
     *
     * @return The lightMap Vector
     */
    fun interpLightMapFrom(lightMap: Vector2f, others: SimpleQuad): Vector2f {
        for (e in 0..1) {
            val p1: Float = others.vertLight[0].get(e)
            val p2: Float = others.vertLight[0].get(e)
            val p3: Float = others.vertLight[0].get(e)
            val p4: Float = others.vertLight[0].get(e)
            if (p1 != p2 || p2 != p3 || p3 != p4) {
                lightMap.set(e, InterpHelper.interpolate(p1, p2, p3, p4))
            }
        }
        return lightMap
    }


    override fun consume(quad: SimpleQuad) {
        val s: Int = quad.face.ordinal shr 1

        if (quad.vertColor.isNotEmpty() || quad.vertUv.isNotEmpty() || quad.vertLight.isNotEmpty()) {
            val originalPositions = unmodifiedQuad!!.vertPos
            val copy = SimpleQuad(quad)

            InterpHelper.reset(originalPositions[0].dx(s), originalPositions[0].dy(s),
                    originalPositions[1].dx(s), originalPositions[1].dy(s),
                    originalPositions[2].dx(s), originalPositions[2].dy(s),
                    originalPositions[3].dx(s), originalPositions[3].dy(s))

            InterpHelper.setup()

            quad.vertPos.forEachIndexed { index, v ->
                InterpHelper.locate(v.dx(s), v.dy(s))
                if (quad.vertColor.isNotEmpty()) {
                    quad.vertColor[index] = interpColorFrom(quad.vertColor[index], copy)
                }
                if (quad.vertUv.isNotEmpty()) {
                    quad.vertUv[index] = interpUVFrom(quad.vertUv[index], copy)
                }
                if (quad.vertLight.isNotEmpty()) {
                    quad.vertLight[index] = interpLightMapFrom(quad.vertLight[index], copy)
                }
            }
        }
    }
}