package net.cydhra.technocracy.foundation.client.model.pipe

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.content.tileentities.pipe.TileEntityPipe
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
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

        val currentLayer: BlockRenderLayer? = MinecraftForgeClient.getRenderLayer()
        val quads = mutableListOf<BakedQuad>()
        if (currentLayer == null)
            return quads

        val pos = (state as IExtendedBlockState).getValue(POSITION)
        val tileEntityPipe = Minecraft.getMinecraft().world.getTileEntity(pos) as TileEntityPipe

        //todo needs lots of caching

        if (currentLayer == BlockRenderLayer.CUTOUT && side == null) {
            val boxes = tileEntityPipe.getPipeModelParts()

            for (it in boxes) {
                val boundingBox = it.first.second
                val facing = it.first.first

                if (it.third == -1)
                    continue

                val texture = when (it.third) {
                    0 -> TextureAtlasManager.pipe_node
                    1 -> TextureAtlasManager.getTextureForConnectionType(it.second!!)
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

                val rotateY = if (facing.axis == EnumFacing.Axis.Y) 1 else 0
                val rotateX = if (facing.axis == EnumFacing.Axis.X) 0 else 1


                val north = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.NORTH)
                north.addPos(maxX, maxY, minZ).addPos(maxX, minY, minZ).addPos(minX, minY, minZ).addPos(minX, maxY, minZ)
                north.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV).rotate(rotateY)
                quads.add(north.bake())

                val south = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.SOUTH)
                south.addPos(minX, maxY, maxZ).addPos(minX, minY, maxZ).addPos(maxX, minY, maxZ).addPos(maxX, maxY, maxZ)
                south.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV).rotate(rotateY)
                quads.add(south.bake())

                val west = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.WEST)
                west.addPos(minX, maxY, minZ).addPos(minX, minY, minZ).addPos(minX, minY, maxZ).addPos(minX, maxY, maxZ)
                west.addUV(maxU, minV).addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).rotate(1 - rotateY)
                quads.add(west.bake())

                val east = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.EAST)
                east.addPos(maxX, maxY, maxZ).addPos(maxX, minY, maxZ).addPos(maxX, minY, minZ).addPos(maxX, maxY, minZ)
                east.addUV(maxU, minV).addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).rotate(1 - rotateY)
                quads.add(east.bake())

                val up = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.UP)
                up.addPos(maxX, maxY, maxZ).addPos(maxX, maxY, minZ).addPos(minX, maxY, minZ).addPos(minX, maxY, maxZ)
                up.addUV(minU, maxV).addUV(minU, minV).addUV(maxU, minV).addUV(maxU, maxV).rotate(rotateX)
                quads.add(up.bake())

                val bottom = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.DOWN)
                bottom.addPos(minX, minY, maxZ).addPos(minX, minY, minZ).addPos(maxX, minY, minZ).addPos(maxX, minY, maxZ)
                bottom.addUV(minU, maxV).addUV(minU, minV).addUV(maxU, minV).addUV(maxU, maxV).rotate(rotateX)
                quads.add(bottom.bake())
            }
        }

        val faces = BooleanArray(EnumFacing.values().size)
        val coverFaces = tileEntityPipe.getFacades()

        EnumFacing.values().forEachIndexed { index, enumFacing ->
            faces[index] = coverFaces.containsKey(enumFacing)
        }

        coverFaces.forEach { face, stack ->
            quads.addAll(FacadeBakery.getFacadeQuads(face, stack, pos, faces, currentLayer))
        }

        return quads
    }
}