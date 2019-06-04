package net.cydhra.technocracy.foundation.client.model.pipe

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.IExtendedBlockState
import org.apache.commons.lang3.tuple.Pair
import javax.vecmath.Matrix4f

class PipeModelBakery : IBakedModel {
    override fun getParticleTexture(): TextureAtlasSprite {
        return TextureAtlasManager.pipe_node
    }

    override fun isBuiltInRenderer(): Boolean {
        return true
    }

    override fun isAmbientOcclusion(): Boolean {
        return true
    }

    override fun isGui3d(): Boolean {
        return true
    }

    override fun getOverrides(): ItemOverrideList {
        return ItemOverrideList.NONE
    }

    override fun handlePerspective(
            cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel, Matrix4f> {
        return Pair.of<IBakedModel, Matrix4f>(this, TRSRTransformation.identity().matrix)
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()

        val tileEntityPipe =
                Minecraft.getMinecraft().world.getTileEntity((state as IExtendedBlockState).getValue(POSITION)) as TileEntityPipe
        val boxes = tileEntityPipe.getPipeModelParts()

        boxes.forEach {
            val boundingBox = it.first.second
            val facing = it.first.first

            val texture = when (it.third) {
                0 -> this.getTextureForNodeType(it.second)
                1 -> this.getTextureForConnectionType(it.second)
                else -> error("Box type out of bounds")
            }

            val minU = texture.minU
            val maxU = texture.maxU
            val minV = texture.minV
            val maxV = texture.maxV

            val minX = boundingBox.minX.toFloat()
            val minY = boundingBox.minY.toFloat()
            val minZ = boundingBox.minZ.toFloat()
            val maxX = boundingBox.maxX.toFloat()
            val maxY = boundingBox.maxY.toFloat()
            val maxZ = boundingBox.maxZ.toFloat()

            //Front
            val quad1 = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad1.setTexture(texture)
            quad1.put(0, maxX, minY, minZ, 1F)
            quad1.put(1, minU, maxV, 0F, 1F)
            quad1.put(2, 1F, 1F, 1F, 1F)
            quad1.put(0, minX, minY, minZ, 1F)
            quad1.put(1, maxU, maxV, 0F, 1F)
            quad1.put(2, 1F, 1F, 1F, 1F)
            quad1.put(0, minX, maxY, minZ, 1F)
            quad1.put(1, maxU, minV, 0F, 1F)
            quad1.put(2, 1F, 1F, 1F, 1F)
            quad1.put(0, maxX, maxY, minZ, 1F)
            quad1.put(1, minU, minV, 0F, 1F)
            quad1.put(2, 1F, 1F, 1F, 1F)
            quads.add(quad1.build())

            //Back
            val quad2 = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad2.setTexture(texture)
            quad2.put(0, minX, minY, maxZ, 1F)
            quad2.put(1, minU, minV, 0F, 1F)
            quad2.put(2, 1F, 1F, 1F, 1F)
            quad2.put(0, maxX, minY, maxZ, 1F)
            quad2.put(1, maxU, minV, 0F, 1F)
            quad2.put(2, 1F, 1F, 1F, 1F)
            quad2.put(0, maxX, maxY, maxZ, 1F)
            quad2.put(1, maxU, maxV, 0F, 1F)
            quad2.put(2, 1F, 1F, 1F, 1F)
            quad2.put(0, minX, maxY, maxZ, 1F)
            quad2.put(1, minU, maxV, 0F, 1F)
            quad2.put(2, 1F, 1F, 1F, 1F)
            quads.add(quad2.build())

            //Left
            val quad3 = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad3.setTexture(texture)
            quad3.put(0, minX, minY, minZ, 1F)
            quad3.put(1, minU, minV, 0F, 1F)
            quad3.put(2, 1F, 1F, 1F, 1F)
            quad3.put(0, minX, minY, maxZ, 1F)
            quad3.put(1, maxU, minV, 0F, 1F)
            quad3.put(2, 1F, 1F, 1F, 1F)
            quad3.put(0, minX, maxY, maxZ, 1F)
            quad3.put(1, maxU, maxV, 0F, 1F)
            quad3.put(2, 1F, 1F, 1F, 1F)
            quad3.put(0, minX, maxY, minZ, 1F)
            quad3.put(1, minU, maxV, 0F, 1F)
            quad3.put(2, 1F, 1F, 1F, 1F)
            quads.add(quad3.build())

            //Right
            val quad4 = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad4.setTexture(texture)
            quad4.put(0, maxX, minY, maxZ, 1F)
            quad4.put(1, minU, minV, 0F, 1F)
            quad4.put(2, 1F, 1F, 1F, 1F)
            quad4.put(0, maxX, minY, minZ, 1F)
            quad4.put(1, maxU, minV, 0F, 1F)
            quad4.put(2, 1F, 1F, 1F, 1F)
            quad4.put(0, maxX, maxY, minZ, 1F)
            quad4.put(1, maxU, maxV, 0F, 1F)
            quad4.put(2, 1F, 1F, 1F, 1F)
            quad4.put(0, maxX, maxY, maxZ, 1F)
            quad4.put(1, minU, maxV, 0F, 1F)
            quad4.put(2, 1F, 1F, 1F, 1F)
            quads.add(quad4.build())

            val quad5 = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad5.setTexture(texture)
            //Top
            if (facing.axis == EnumFacing.Axis.X) {
                quad5.put(0, maxX, maxY, minZ, 1F)
                quad5.put(1, minU, maxV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
                quad5.put(0, minX, maxY, minZ, 1F)
                quad5.put(1, maxU, maxV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
                quad5.put(0, minX, maxY, maxZ, 1F)
                quad5.put(1, maxU, minV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
                quad5.put(0, maxX, maxY, maxZ, 1F)
                quad5.put(1, minU, minV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
            } else {
                quad5.put(0, maxX, maxY, minZ, 1F)
                quad5.put(1, minU, minV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
                quad5.put(0, minX, maxY, minZ, 1F)
                quad5.put(1, minU, maxV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
                quad5.put(0, minX, maxY, maxZ, 1F)
                quad5.put(1, maxU, maxV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
                quad5.put(0, maxX, maxY, maxZ, 1F)
                quad5.put(1, maxU, minV, 0F, 1F)
                quad5.put(2, 1F, 1F, 1F, 1F)
            }
            quads.add(quad5.build())

            val quad6 = UnpackedBakedQuad.Builder(DefaultVertexFormats.POSITION_TEX_COLOR)
            quad6.setTexture(texture)
            //Down
            if (facing.axis == EnumFacing.Axis.X) {
                quad6.put(0, minX, minY, minZ, 1F)
                quad6.put(1, minU, maxV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
                quad6.put(0, maxX, minY, minZ, 1F)
                quad6.put(1, maxU, maxV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
                quad6.put(0, maxX, minY, maxZ, 1F)
                quad6.put(1, maxU, minV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
                quad6.put(0, minX, minY, maxZ, 1F)
                quad6.put(1, minU, minV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
            } else {
                quad6.put(0, minX, minY, minZ, 1F)
                quad6.put(1, minU, minV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
                quad6.put(0, maxX, minY, minZ, 1F)
                quad6.put(1, minU, maxV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
                quad6.put(0, maxX, minY, maxZ, 1F)
                quad6.put(1, maxU, maxV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
                quad6.put(0, minX, minY, maxZ, 1F)
                quad6.put(1, maxU, minV, 0F, 1F)
                quad6.put(2, 1F, 1F, 1F, 1F)
            }
            quads.add(quad6.build())
        }

        return quads
    }

    /**
     * Returns the texture for connections of a specific pipe type
     */
    private fun getTextureForConnectionType(type: PipeType): TextureAtlasSprite {
        return when (type) {
            PipeType.ENERGY -> TextureAtlasManager.pipe_energy
            PipeType.FLUID -> TextureAtlasManager.pipe_fluid
            PipeType.ITEM -> TextureAtlasManager.pipe_item
        }
    }

    /**
     * Returns the texture for nodes of a specific pipe type
     */
    private fun getTextureForNodeType(type: PipeType): TextureAtlasSprite {
        return TextureAtlasManager.pipe_node
    }
}