package net.cydhra.technocracy.foundation.client.model.tank

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.propertys.DIMENSIONS
import net.cydhra.technocracy.foundation.util.propertys.FLUIDSTACK
import net.cydhra.technocracy.foundation.util.propertys.TANKSIZE
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState


class MutliBlockTankFluidModelBakery(val baseModel: IBakedModel) : IBakedModel by baseModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {

        val quads = baseModel.getQuads(state, side, rand)
        val extended = state as? IExtendedBlockState ?: return quads

        val stack = extended.getValue(FLUIDSTACK) ?: return quads
        val dimension = extended.getValue(DIMENSIONS) ?: return quads
        val maxSize = extended.getValue(TANKSIZE) ?: return quads

        val texture = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(stack.fluid.still.toString())

        val percentage = stack.amount / maxSize
        val height = dimension.y * percentage

        val minU = texture.minU
        val maxU = texture.maxU
        val minV = texture.minV
        val maxV = texture.maxV

        val minX = -(dimension.x / 2)
        val minY = -(dimension.y / 2)
        val minZ = -(dimension.z / 2)
        val maxX = dimension.x / 2
        val maxY = dimension.y / 2
        val maxZ = dimension.z / 2

        val north = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.NORTH)
        north.addPos(maxX, maxY, minZ).addPos(maxX, minY, minZ).addPos(minX, minY, minZ).addPos(minX, maxY, minZ)
        north.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV)
        quads.add(north.bake())

        val south = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.SOUTH)
        south.addPos(minX, maxY, maxZ).addPos(minX, minY, maxZ).addPos(maxX, minY, maxZ).addPos(maxX, maxY, maxZ)
        south.addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV).addUV(maxU, minV)
        quads.add(south.bake())

        val west = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.WEST)
        west.addPos(minX, maxY, minZ).addPos(minX, minY, minZ).addPos(minX, minY, maxZ).addPos(minX, maxY, maxZ)
        west.addUV(maxU, minV).addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV)
        quads.add(west.bake())

        val east = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.EAST)
        east.addPos(maxX, maxY, maxZ).addPos(maxX, minY, maxZ).addPos(maxX, minY, minZ).addPos(maxX, maxY, minZ)
        east.addUV(maxU, minV).addUV(minU, minV).addUV(minU, maxV).addUV(maxU, maxV)
        quads.add(east.bake())

        val up = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.UP)
        up.addPos(maxX, maxY, maxZ).addPos(maxX, maxY, minZ).addPos(minX, maxY, minZ).addPos(minX, maxY, maxZ)
        up.addUV(minU, maxV).addUV(minU, minV).addUV(maxU, minV).addUV(maxU, maxV)
        quads.add(up.bake())

        val bottom = SimpleQuad(DefaultVertexFormats.POSITION_TEX_COLOR).setTexture(texture).setFace(EnumFacing.DOWN)
        bottom.addPos(minX, minY, maxZ).addPos(minX, minY, minZ).addPos(maxX, minY, minZ).addPos(maxX, minY, maxZ)
        bottom.addUV(minU, maxV).addUV(minU, minV).addUV(maxU, minV).addUV(maxU, maxV)
        quads.add(bottom.bake())

        return quads
    }
}