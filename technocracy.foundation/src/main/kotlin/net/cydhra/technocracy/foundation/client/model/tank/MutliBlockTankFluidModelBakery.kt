package net.cydhra.technocracy.foundation.client.model.tank

import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.QuadPipeline
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.QuadOffsetter
import net.cydhra.technocracy.foundation.util.propertys.DIMENSIONS
import net.cydhra.technocracy.foundation.util.propertys.FLUIDSTACK
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.cydhra.technocracy.foundation.util.propertys.TANKSIZE
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fluids.FluidStack


class MutliBlockTankFluidModelBakery(val baseModel: IBakedModel) : IBakedModel by baseModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {

        val currentLayer = MinecraftForgeClient.getRenderLayer()

        if (state == null || (side != null && state.block.getBlockLayer() == currentLayer)) {
            return baseModel.getQuads(state, side, rand)
        }

        val quads = mutableListOf<BakedQuad>()

        val extended = state as? IExtendedBlockState ?: return quads

        val stack = extended.getValue(FLUIDSTACK) ?: return quads

        val pos = extended.getValue(POSITION) ?: return quads

        val renderLayer = stack.fluid.block?.blockLayer ?: BlockRenderLayer.TRANSLUCENT

        if (currentLayer != renderLayer)
            return quads

        val dimension = extended.getValue(DIMENSIONS) ?: return quads
        //use Integer as it causes class cast exception
        val maxSize = extended.getValue(TANKSIZE) ?: return quads

        val percentage = stack.amount / maxSize.toFloat()
        val height = (dimension.y - 2) * percentage

        val minX = -(dimension.x / 2)
        val minY = 1.001f
        val minZ = -(dimension.z / 2)
        val maxX = dimension.x / 2
        val maxY = height + 1f
        val maxZ = dimension.z / 2

        val gen = generateFluidCube(stack, minX, minY, minZ, maxX, maxY, maxZ, 0.51f, pos)

        val pipeLine = QuadPipeline().addConsumer(QuadOffsetter)
        QuadOffsetter.offsetX = -dimension.x / 2
        QuadOffsetter.offsetZ = -dimension.z / 2

        for (quad in gen) {
            quads.add(pipeLine.pipe(quad).bake())
        }

        QuadOffsetter.reset()

        return quads
    }

    fun generateFluidCube(fluid: FluidStack, minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, offsetToBlockEdge: Float, pos: BlockPos): MutableList<SimpleQuad> {

        val mc = Minecraft.getMinecraft()

        val color = fluid.fluid.getColor(fluid)
        //val brightness = mc.world.getCombinedLight(pos, fluid.fluid.luminosity)

        val i: Int = mc.world.getCombinedLight(pos, fluid.fluid.luminosity)
        val j: Int = mc.world.getCombinedLight(pos.up(), fluid.fluid.luminosity)
        val k = i and 255
        val l = j and 255
        val i1 = i shr 16 and 255
        val j1 = j shr 16 and 255
        val brightness = (if (k > l) k else l) or (if (i1 > j1) i1 else j1) shl 16

        var still = mc.textureMapBlocks.getTextureExtry(fluid.fluid.getStill(fluid).toString())
        var flowing = mc.textureMapBlocks.getTextureExtry(fluid.fluid.getFlowing(fluid).toString())

        if (still == null) {
            still = mc.textureMapBlocks.missingSprite
        }
        if (flowing == null) {
            flowing = mc.textureMapBlocks.missingSprite
        }
        val upsideDown = fluid.fluid.isGaseous(fluid)

        val xd = (maxX - minX).toInt()

        // the liquid can stretch over more blocks than the subtracted height is if ymin's decimal is bigger than ymax's decimal (causing UV over 1)
        // ignoring the decimals prevents this, as yd then equals exactly how many ints are between the two
        // for example, if ymax = 5.1 and ymin = 2.3, 2.8 (which rounds to 2), with the face array becoming 2.3, 3, 4, 5.1
        val yminInt = minY.toInt()
        var yd = (maxY - yminInt).toInt()

        // prevents a rare case of rendering the top face multiple times if ymax is perfectly aligned with the block
        // for example, if ymax = 3 and ymin = 1, the values of the face array become 1, 2, 3, 3 as we then have middle ints
        if (maxY % 1.0 == 0.0) yd--
        val zd = (maxZ - minZ).toInt()

        val xmin = offsetToBlockEdge.toDouble()
        val xmax = xd + 1.0 - offsetToBlockEdge
        val zmin = offsetToBlockEdge.toDouble()
        val zmax = zd + 1.0 - offsetToBlockEdge

        val xs = DoubleArray(2 + xd)
        val ys = DoubleArray(2 + yd)
        val zs = DoubleArray(2 + zd)

        xs[0] = xmin
        for (i in 1..xd) xs[i] = i.toDouble()
        xs[xd + 1] = xmax

        // we have to add the whole number for ymin or otherwise things render incorrectly if above the first block
        // example, heights of 2 and 5 would produce array of 2, 1, 2, 5
        ys[0] = minY.toDouble()
        for (i in 1..yd) ys[i] = (i + yminInt).toDouble()
        ys[yd + 1] = maxY.toDouble()

        zs[0] = zmin
        for (i in 1..zd) zs[i] = i.toDouble()
        zs[zd + 1] = zmax

        val list = mutableListOf<SimpleQuad>()

        // render each side
        for (y in 0..yd) {
            for (z in 0..zd) {
                for (x in 0..xd) {

                    val x1 = xs[x].toFloat()
                    val x2 = (xs[x + 1] - x1).toFloat()
                    val y1 = ys[y].toFloat()
                    val y2 = (ys[y + 1] - y1).toFloat()
                    val z1 = zs[z].toFloat()
                    val z2 = (zs[z + 1] - z1).toFloat()

                    if (x == 0) putTexturedQuad(list, still, x1, y1, z1, x2, y2, z2, EnumFacing.WEST, color, brightness, false, upsideDown)
                    if (x == xd) putTexturedQuad(list, still, x1, y1, z1, x2, y2, z2, EnumFacing.EAST, color, brightness, false, upsideDown)
                    if (y == 0) putTexturedQuad(list, still, x1, y1, z1, x2, y2, z2, EnumFacing.DOWN, color, brightness, false, upsideDown)
                    if (y == yd) putTexturedQuad(list, still, x1, y1, z1, x2, y2, z2, EnumFacing.UP, color, brightness, false, upsideDown)
                    if (z == 0) putTexturedQuad(list, still, x1, y1, z1, x2, y2, z2, EnumFacing.NORTH, color, brightness, false, upsideDown)
                    if (z == zd) putTexturedQuad(list, still, x1, y1, z1, x2, y2, z2, EnumFacing.SOUTH, color, brightness, false, upsideDown)
                }
            }
        }

        return list
    }

    fun putTexturedQuad(list: MutableList<SimpleQuad>, sprite: TextureAtlasSprite?, x: Float, y: Float, z: Float, w: Float, h: Float, d: Float, face: EnumFacing,
                        color: Int, brightness: Int, flowing: Boolean, flipHorizontally: Boolean) {

        val sky = brightness shr 0x04 and 0xF
        val block = brightness shr 0x14 and 0xF

        val a = color shr 24 and 0xFF
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color and 0xFF

        putTexturedQuad(list, sprite, x, y, z, w, h, d, face, r, g, b, a, block, sky, flowing, flipHorizontally)
    }

    fun putTexturedQuad(list: MutableList<SimpleQuad>, sprite: TextureAtlasSprite?, x: Float, y: Float, z: Float, w: Float, h: Float, d: Float, face: EnumFacing,
                        r: Int, g: Int, b: Int, a: Int, block: Int, sky: Int, flowing: Boolean, flipHorizontally: Boolean) {

        val r = r / 255f
        val g = g / 255f
        val b = b / 255f
        val a = 0.8f//a / 255f

        // safety
        if (sprite == null) {
            return
        }
        val minU: Float
        val maxU: Float
        var minV: Float
        var maxV: Float

        var size = 16.0
        if (flowing) {
            size = 8.0
        }

        val x2 = x + w
        val y2 = y + h
        val z2 = z + d

        val xt1 = x % 1.0
        var xt2 = xt1 + w
        while (xt2 > 1f) xt2 -= 1.0
        var yt1 = y % 1.0
        var yt2 = yt1 + h
        while (yt2 > 1f) yt2 -= 1.0
        val zt1 = z % 1.0
        var zt2 = zt1 + d
        while (zt2 > 1f) zt2 -= 1.0

        //start from the bottom, not from the top
        val tmp = 1.0 - yt1
        yt1 = 1.0 - yt2
        yt2 = tmp

        when (face) {
            EnumFacing.DOWN, EnumFacing.UP -> {
                minU = sprite.getInterpolatedU(xt1 * size)
                maxU = sprite.getInterpolatedU(xt2 * size)
                minV = sprite.getInterpolatedV(zt1 * size)
                maxV = sprite.getInterpolatedV(zt2 * size)
            }
            EnumFacing.NORTH, EnumFacing.SOUTH -> {
                minU = sprite.getInterpolatedU(xt2 * size)
                maxU = sprite.getInterpolatedU(xt1 * size)
                minV = sprite.getInterpolatedV(yt1 * size)
                maxV = sprite.getInterpolatedV(yt2 * size)
            }
            EnumFacing.WEST, EnumFacing.EAST -> {
                minU = sprite.getInterpolatedU(zt2 * size)
                maxU = sprite.getInterpolatedU(zt1 * size)
                minV = sprite.getInterpolatedV(yt1 * size)
                maxV = sprite.getInterpolatedV(yt2 * size)
            }
            else -> {
                minU = sprite.minU
                maxU = sprite.maxU
                minV = sprite.minV
                maxV = sprite.maxV
            }
        }

        if (flipHorizontally) {
            minV = maxV
            maxV = minV
        }

        val quad = SimpleQuad(DefaultVertexFormats.BLOCK).setTexture(sprite).setFace(face)

        quad.addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a).addColor(r, g, b, a)
        quad.addLight(block, sky).addLight(block, sky).addLight(block, sky).addLight(block, sky)

        when (face) {
            EnumFacing.DOWN -> {
                quad.addPos(x, y, z).addUV(minU, minV)
                quad.addPos(x2, y, z).addUV(maxU, minV)
                quad.addPos(x2, y, z2).addUV(maxU, maxV)
                quad.addPos(x, y, z2).addUV(minU, maxV)
            }
            EnumFacing.UP -> {
                quad.addPos(x, y2, z).addUV(minU, minV)
                quad.addPos(x, y2, z2).addUV(minU, maxV)
                quad.addPos(x2, y2, z2).addUV(maxU, maxV)
                quad.addPos(x2, y2, z).addUV(maxU, minV)
            }
            EnumFacing.NORTH -> {
                quad.addPos(x, y, z).addUV(minU, maxV)
                quad.addPos(x, y2, z).addUV(minU, minV)
                quad.addPos(x2, y2, z).addUV(maxU, minV)
                quad.addPos(x2, y, z).addUV(maxU, maxV)
            }
            EnumFacing.SOUTH -> {
                quad.addPos(x, y, z2).addUV(maxU, maxV)
                quad.addPos(x2, y, z2).addUV(minU, maxV)
                quad.addPos(x2, y2, z2).addUV(minU, minV)
                quad.addPos(x, y2, z2).addUV(maxU, minV)
            }
            EnumFacing.WEST -> {
                quad.addPos(x, y, z).addUV(maxU, maxV)
                quad.addPos(x, y, z2).addUV(minU, maxV)
                quad.addPos(x, y2, z2).addUV(minU, minV)
                quad.addPos(x, y2, z).addUV(maxU, minV)
            }
            EnumFacing.EAST -> {
                quad.addPos(x2, y, z).addUV(minU, maxV)
                quad.addPos(x2, y2, z).addUV(minU, minV)
                quad.addPos(x2, y2, z2).addUV(maxU, minV)
                quad.addPos(x2, y, z2).addUV(maxU, maxV)
            }
        }

        list.add(quad)
    }

    override fun isAmbientOcclusion(): Boolean {
        return false
    }
}