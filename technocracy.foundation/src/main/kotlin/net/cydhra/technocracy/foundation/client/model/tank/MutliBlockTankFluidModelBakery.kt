package net.cydhra.technocracy.foundation.client.model.tank

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.QuadPipeline
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.QuadShrinker
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.QuadTinter
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.QuadUVTransformer
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.clone.QuadCloneConsumer
import net.cydhra.technocracy.foundation.util.opengl.OpenGLFluidRenderer
import net.cydhra.technocracy.foundation.util.propertys.DIMENSIONS
import net.cydhra.technocracy.foundation.util.propertys.FLUIDSTACK
import net.cydhra.technocracy.foundation.util.propertys.TANKSIZE
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.property.IExtendedBlockState


class MutliBlockTankFluidModelBakery(val baseModel: IBakedModel) : IBakedModel by baseModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {

        val currentLayer = MinecraftForgeClient.getRenderLayer()

        if (state == null || (side != null && state.block.getBlockLayer() == currentLayer)) {
            return baseModel.getQuads(state, side, rand)
        }

        val quads = mutableListOf<BakedQuad>()

        val extended = state as? IExtendedBlockState ?: return quads

        val stack = extended.getValue(FLUIDSTACK) ?: return quads

        val renderLayer = stack.fluid.block?.blockLayer ?: BlockRenderLayer.TRANSLUCENT

        if (currentLayer != renderLayer)
            return quads

        val dimension = extended.getValue(DIMENSIONS) ?: return quads
        //use Integer as it causes class cast exception
        val maxSize = extended.getValue(TANKSIZE) ?: return quads

        //val texture = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(stack.fluid.still.toString())
        val texture = TextureAtlasManager.getTextureAtlasSprite(stack.fluid.still)

        val percentage = stack.amount / maxSize.toFloat()
        val height = (dimension.y - 2) * percentage

        val minU = texture.minU
        val maxU = texture.maxU
        val minV = texture.minV
        val maxV = texture.maxV

        val minX = -(dimension.x / 2) + 0.51f
        val minY = 1.001f
        val minZ = -(dimension.z / 2) + 0.51f
        val maxX = dimension.x / 2 + 0.49f
        val maxY = height + 1f
        val maxZ = dimension.z / 2 + 0.49f

        val pipeline = QuadPipeline().addConsumer()

        val north = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture)

        val b = (stack.fluid.color and 0xFF) / 255f
        val g = (stack.fluid.color shr 8 and 0xFF) / 255f
        val r = (stack.fluid.color shr 16 and 0xFF) / 255f
        val a = 0.8f

        north.addPos(maxX, maxY, minZ).addPos(maxX, minY, minZ).addPos(minX, minY, minZ).addPos(minX, maxY, minZ)
        north.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV)
        north.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quads.add(pipeline.pipe(north).bake())

        val south = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.SOUTH)
        south.addPos(minX, maxY, maxZ).addPos(minX, minY, maxZ).addPos(maxX, minY, maxZ).addPos(maxX, maxY, maxZ)
        //south.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV)
        south.addUV(maxU, minV).addUV(maxU, maxV).addUV(minU, maxV).addUV(minU, minV)
        south.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quads.add(pipeline.pipe(south).bake())

        val west = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.WEST)
        west.addPos(minX, maxY, minZ).addPos(minX, minY, minZ).addPos(minX, minY, maxZ).addPos(minX, maxY, maxZ)
        //west.addUV(maxU, minV).addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV)
        west.addUV(maxU, minV).addUV(maxU, maxV).addUV(minU, maxV).addUV(minU, minV)
        west.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quads.add(pipeline.pipe(west).bake())

        val east = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.EAST)
        east.addPos(maxX, maxY, maxZ).addPos(maxX, minY, maxZ).addPos(maxX, minY, minZ).addPos(maxX, maxY, minZ)
        //east.addUV(maxU, minV).addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).rotate(1)
        east.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV)
        east.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quads.add(pipeline.pipe(east).bake())

        val up = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.UP)
        up.addPos(maxX, maxY, maxZ).addPos(maxX, maxY, minZ).addPos(minX, maxY, minZ).addPos(minX, maxY, maxZ)
        up.addUV(minU, maxV).addUV(minU, minV).addUV(maxU, minV).addUV(maxU, maxV)
        up.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quads.add(pipeline.pipe(up).bake())

        val bottom = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.DOWN)
        bottom.addPos(minX, minY, maxZ).addPos(minX, minY, minZ).addPos(maxX, minY, minZ).addPos(maxX, minY, maxZ)
        bottom.addUV(minU, maxV).addUV(minU, minV).addUV(maxU, minV).addUV(maxU, maxV)
        bottom.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quads.add(pipeline.pipe(bottom).bake())



        return quads
    }
}