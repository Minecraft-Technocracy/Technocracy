package net.cydhra.technocracy.foundation.util.model

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.IVertexConsumer
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f


class QuadCloner(val quad: SimpleQuad) : IVertexConsumer {
    override fun getVertexFormat(): VertexFormat {
        return quad.format
    }

    override fun put(element: Int, vararg data: Float) {
        val usage = vertexFormat.getElement(element)
        if (usage.usage == VertexFormatElement.EnumUsage.UV) {
            if (usage.index == 1) {
                if (data.isNotEmpty())
                    quad.vertLight.add(Vector2f(data[0], data[1]))
                else
                    quad.data.put(usage.usage, data)
            } else if (usage.index == 0) {
                quad.vertUv.add(Vector2f(data[0], data[1]))
            }
        } else if (usage.usage == VertexFormatElement.EnumUsage.COLOR && data.isNotEmpty()) {
            quad.vertColor.add(Vector4f(data[0], data[1], data[2], data[3]))
        } else if (usage.usage == VertexFormatElement.EnumUsage.NORMAL && data.isNotEmpty()) {
            quad.vertNormal.add(Vector3f(data[0], data[1], data[2]))
        } else if (quad.clonePosData && usage.usage == VertexFormatElement.EnumUsage.POSITION) {
            quad.vertPos.add(Vector3f(data[0], data[1], data[2]))
        } else
            if (usage.usage != VertexFormatElement.EnumUsage.POSITION) {

                usage.usage

                quad.data.put(usage.usage, data)
            }
    }

    override fun setQuadOrientation(orientation: EnumFacing?) {
    }

    override fun setTexture(texture: TextureAtlasSprite?) {
    }

    override fun setApplyDiffuseLighting(diffuse: Boolean) {
    }

    override fun setQuadTint(tint: Int) {
    }

}