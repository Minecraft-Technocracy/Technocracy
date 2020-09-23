package net.cydhra.technocracy.foundation.client.model.pipe

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.items.FacadeItem
import net.cydhra.technocracy.foundation.data.config.RenderConfig
import net.cydhra.technocracy.foundation.util.facade.FakeBlockAccess
import net.cydhra.technocracy.foundation.util.intFromBools
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.QuadPipeline
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.*
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.clone.QuadCloneConsumer
import net.minecraft.block.Block
import net.minecraft.block.BlockDirectional
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object FacadeBakery {

    val quadCache = mutableMapOf<Triple<EnumFacing, IBakedModel, BlockRenderLayer>, MutableMap<Int, List<BakedQuad>>>()
    val ctmQuadCache = mutableMapOf<Triple<Int, EnumFacing?, EnumFacing>, MutableList<SimpleQuad>>()

    @Suppress("DEPRECATION")//no other method available to get state from meta
    fun getFacadeQuads(coverFace: EnumFacing, facadeStack: ItemStack, pos: BlockPos, faces: BooleanArray, currentLayer: BlockRenderLayer): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()

        val mc = Minecraft.getMinecraft()
        val dispatcher = mc.blockRendererDispatcher

        if (facadeStack.isEmpty || facadeStack.item !is FacadeItem)
            return quads

        val facade = facadeStack.item as FacadeItem
        val stack = facade.getFacadeFromStack(facadeStack)

        val block = Block.getBlockFromItem(stack.stack.item)
        var state: IBlockState = block.getStateFromMeta(stack.stack.itemDamage)

        if (state.properties[BlockDirectional.FACING] != null || state.properties[BlockHorizontal.FACING] != null) {
            state = block.getStateFromMeta(coverFace.ordinal)
        }

        if (state.block.canRenderInLayer(state, currentLayer)) {
            //fix for ctm not working
            val fakeWorld = FakeBlockAccess(mc.world, state, pos)

            //get the custom state with ctm data
            val customState = state.getActualState(fakeWorld, pos)
            val coverModel = dispatcher.getModelForState(customState)
            var extendedState = customState.block.getExtendedState(customState, fakeWorld, pos)

            if(RenderConfig.aggressiveFacadeCaching) {
                extendedState = customState
            }


            //eh they are probably doing something fishy so dont cache
            if (!RenderConfig.aggressiveFacadeCaching && customState != extendedState) {
                try {
                    quads.addAll(genQuads(coverModel, extendedState, coverFace, faces, fakeWorld, pos))
                } catch (e: Exception) {
                    e.printStackTrace()
                    TCFoundation.logger.error("Facade of block ${extendedState.block} has special renderer and references a tile entity that is not in the world")
                }
            } else {
                val map = quadCache.getOrPut(Triple(coverFace, coverModel, currentLayer)) { mutableMapOf() }

                val cacheKey = intFromBools(*faces)

                var cache = map[cacheKey]

                if (cache == null) {
                    try {
                        cache = genQuads(coverModel, extendedState, coverFace, faces, fakeWorld, pos)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        TCFoundation.logger.error("Facade of block ${extendedState.block} has special renderer and references a tile entity that is not in the world")
                    }
                    if (cache == null)
                        cache = emptyList()
                    map[cacheKey] = cache
                }

                quads.addAll(cache)
            }
        }

        return quads
    }

    fun getFacadeItemQuads(facadeItem: ItemStack): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()
        val coverModel = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(facadeItem, Minecraft.getMinecraft().world, null)
        val faces = BooleanArray(EnumFacing.values().size)

        EnumFacing.values().forEachIndexed { index, enumFacing ->
            faces[index] = enumFacing == EnumFacing.NORTH
        }

        val origQuads = mutableListOf<BakedQuad>()
        for (face in EnumFacing.VALUES) {
            origQuads.addAll(coverModel.getQuads(null, face, 0))
        }
        origQuads.addAll(coverModel.getQuads(null, null, 0))

        val tinter = QuadTinter()

        val pipeline = QuadPipeline().addConsumer(QuadCloneConsumer(true), tinter, QuadShrinker(EnumFacing.NORTH, faces), QuadUVTransformer)

        for (bakedQuad in origQuads) {
            val quad = SimpleQuad(DefaultVertexFormats.ITEM)

            tinter.tint = Minecraft.getMinecraft().itemColors.colorMultiplier(facadeItem, bakedQuad.tintIndex)

            pipeline.pipe(quad, bakedQuad)
            quads.add(quad.bake())
        }

        return quads
    }

    fun genQuads(coverModel: IBakedModel, customState: IBlockState, coverFace: EnumFacing, faces: BooleanArray, access: IBlockAccess, pos: BlockPos): List<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()
        var origQuads = coverModel.getQuads(customState, null, 0)

        val tinter = QuadTinter()

        //TODO rework the QuadFacadeTransformer so it uses the vertex position for the translation instead of just the facing
        val pipeline = QuadPipeline().addConsumer(QuadCloneConsumer(true), tinter, QuadShrinker(coverFace, faces), QuadFacadeTransformer(coverFace, faces), QuadUVTransformer)

        //TODO cleanup
        if (origQuads.isNotEmpty()) {
            //Custom model
            origQuads.forEachIndexed { _, bakedQuad ->
                val quad = SimpleQuad(DefaultVertexFormats.BLOCK)
                tinter.tint = Minecraft.getMinecraft().blockColors.colorMultiplier(customState, access, pos, bakedQuad.tintIndex)
                quads.add(pipeline.pipe(quad, bakedQuad).bake())
            }
        } else {

            val tintCache = mutableMapOf<Int, Int>()

            for (side in EnumFacing.values()) {
                origQuads = coverModel.getQuads(customState, side, 0)
                if (origQuads.isNotEmpty()) {
                    try {
                        if (origQuads.size != 4) {
                            //Normal block
                            origQuads.forEachIndexed { _, bakedQuad ->
                                val quad = SimpleQuad(DefaultVertexFormats.BLOCK)
                                tinter.tint = tintCache.getOrPut(bakedQuad.tintIndex) { Minecraft.getMinecraft().blockColors.colorMultiplier(customState, access, pos, bakedQuad.tintIndex) }
                                quads.add(pipeline.pipe(quad, bakedQuad).bake())
                            }
                        } else {

                            val pipeline = QuadPipeline().addConsumer(QuadCloneConsumer(false), QuadDynamicTransformer {
                                //generate a copy of the original quad and set it as the unmodified one to fix texture issues
                                val pipe = QuadPipeline().addConsumer(QuadCloneConsumer(true))
                                val q = SimpleQuad(DefaultVertexFormats.BLOCK)
                                pipe.pipe(q, origQuad!!)
                                unmodifiedQuad = q
                            }, tinter, QuadUVTransformer)

                            //ctm block
                            val splits = generate(faces, coverFace, side)
                            origQuads.forEachIndexed { index, bakedQuad ->
                                splits[index].format = DefaultVertexFormats.BLOCK
                                tinter.tint = tintCache.getOrPut(bakedQuad.tintIndex) { Minecraft.getMinecraft().blockColors.colorMultiplier(customState, access, pos, bakedQuad.tintIndex) }
                                quads.add(pipeline.pipe(splits[index], bakedQuad).bake())
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return quads
    }

    var facadeSize = 1f


    /**
     * Pre generate the quads used for the ctm block
     */
    fun generate(faces: BooleanArray, coverFace: EnumFacing?, facing: EnumFacing): List<SimpleQuad> {

        val cache = ctmQuadCache.getOrPut(Triple(intFromBools(*faces), coverFace, facing)) { mutableListOf() }

        if (cache.isEmpty()) {

            val verts = mutableListOf<FloatArray>()

            for (vertices in 0..3) {
                val data = FloatArray(3)

                val pixelSize = 1 / 16f
                val height = pixelSize * facadeSize

                val minX = 0.0f
                val minY = 0.0f
                val minZ = 0.0f
                val maxX = 1.0f
                val maxY = 1.0f
                val maxZ = 1.0f

                val up = faces[EnumFacing.UP.ordinal]
                val down = faces[EnumFacing.DOWN.ordinal]
                val north = faces[EnumFacing.NORTH.ordinal]
                val south = faces[EnumFacing.SOUTH.ordinal]
                val east = faces[EnumFacing.EAST.ordinal]
                val west = faces[EnumFacing.WEST.ordinal]

                val yWidth = when (coverFace) {
                    EnumFacing.UP -> -(1 - height)
                    EnumFacing.DOWN -> 1 - height
                    else -> 0.0f
                }

                val xWidth = when (coverFace) {
                    EnumFacing.EAST -> -(1 - height)
                    EnumFacing.WEST -> 1 - height
                    else -> 0.0f
                }

                val zWidth = when (coverFace) {
                    EnumFacing.SOUTH -> -(1 - height)
                    EnumFacing.NORTH -> 1 - height
                    else -> 0.0f
                }

                if (facing == EnumFacing.NORTH) {
                    when (vertices) {
                        1 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        2 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        3 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        0 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                    }
                }

                if (facing == EnumFacing.SOUTH) {
                    when (vertices) {
                        1 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        2 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        3 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        0 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                    }
                }

                if (facing == EnumFacing.WEST) {
                    when (vertices) {
                        1 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        2 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        3 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        0 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                    }
                }

                if (facing == EnumFacing.EAST) {
                    when (vertices) {
                        1 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        2 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        3 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        0 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                    }
                }

                if (facing == EnumFacing.UP) {
                    when (vertices) {
                        0 -> {//2
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        1 -> {//3
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        2 -> {//0
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        3 -> {//1
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.DOWN)) height else 0.0f
                            data[1] = maxY - yWidth - if (up && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.DOWN || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                    }
                }

                if (facing == EnumFacing.DOWN) {
                    when (vertices) {
                        1 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                        2 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.SOUTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = minZ - zWidth + if (north && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        3 -> {
                            data[0] = maxX - xWidth - if (east && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.WEST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.WEST)) height else 0.0f
                        }
                        0 -> {
                            data[0] = minX - xWidth + if (west && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.UP)) height else 0.0f
                            data[1] = minY - yWidth + if (down && (coverFace == EnumFacing.NORTH || coverFace == EnumFacing.EAST)) height else 0.0f
                            data[2] = maxZ - zWidth - if (south && (coverFace == EnumFacing.UP || coverFace == EnumFacing.EAST)) height else 0.0f
                        }
                    }
                }

                //clamp

                data[0] = MathHelper.clamp(data[0], 0f, 1f)
                data[1] = MathHelper.clamp(data[1], 0f, 1f)
                data[2] = MathHelper.clamp(data[2], 0f, 1f)
                verts.add(data)
            }

            cache.addAll(SimpleQuad(verts).subdivide(4))
        }

        val copyCache = mutableListOf<SimpleQuad>()

        for (quad in cache) {
            copyCache.add(SimpleQuad(quad.vertPos, quad))
        }

        return copyCache
    }
}