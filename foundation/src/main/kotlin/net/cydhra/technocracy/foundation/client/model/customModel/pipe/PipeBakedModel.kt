package net.cydhra.technocracy.foundation.client.model.customModel.pipe

import net.cydhra.technocracy.foundation.blocks.general.pipe
import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.SimpleBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.MinecraftError
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.property.IExtendedBlockState
import scala.tools.nsc.doc.model.ModelFactory
import java.util.*
import java.util.function.Predicate
import kotlin.collections.ArrayList


class PipeBakedModel(val baseBakedModel: IBakedModel, val models: MutableMap<String, IBakedModel>) : IBakedModel by
baseBakedModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {

        val blockState = state as IExtendedBlockState
        val clean = blockState.clean
        val pos = state.getValue(POSITION)

        val tile = Minecraft.getMinecraft().world.getTileEntity(pos) as TileEntityPipe

        val installedTiles = tile.getInstalledTypes()
        val size = installedTiles.size

        //add the model quads to a list
        val quads = mutableListOf<BakedQuad>()

        val name = when (size) {
            1 -> {
                "single"
            }
            2 -> {
                "double"
            }
            3 -> {
                "triple"
            }
            4 -> {
                "quad"
            }
            else -> {
                null
            }
        } ?: return mutableListOf()

        installedTiles.sorted().forEachIndexed { index, pipeType ->
            val extension = getExtension(pos, Minecraft.getMinecraft().world, pipeType)
            if (extension.stream().filter { it }.findFirst().isPresent) {
                extension.forEachIndexed { extensionIndex, b ->
                    if (b) {
                        val model = models[name + "_extended_" + EnumFacing.values()[extensionIndex].name]

                        if (model != null) {
                            val texture = getTexture(pipeType)
                            val baked = getModelWithTexture(clean, model, texture)
                            val current = baked.getQuads(clean, side, rand)

                            current.forEachIndexed { quadIndex, bakedQuad ->
                                if (quadIndex / 6 == index) {
                                    quads.add(bakedQuad)
                                }
                            }
                        }
                    }
                }
            }

            val model = models[name + "_normal"] ?: return Collections.emptyList()

            val texture = getTexture(pipeType)
            val baked = getModelWithTexture(clean, model, texture)
            val current = baked.getQuads(clean, side, rand)

            current.forEachIndexed { quadIndex, bakedQuad ->
                if (quadIndex / 6 == index) {
                    quads.add(bakedQuad)
                }
            }

        }
        return quads
    }

    fun getExtension(pos: BlockPos, world: World, type: Network.PipeType): ArrayList<Boolean> {
        val sides = arrayListOf(false, false, false, false, false, false)

        EnumFacing.values().forEachIndexed { index, facing ->
            if (world.getBlockState(pos.offset(facing)).block == pipe) {
                val tile = world.getTileEntity(pos.offset(facing)) as TileEntityPipe
                if (tile.getInstalledTypes().contains(type))
                    sides[index] = true
            }
        }
        return sides
    }

    fun getTexture(type: Network.PipeType): TextureAtlasSprite {
        return when (type) {

            Network.PipeType.ENERGY -> TextureAtlasManager.pipe_energy!!
            Network.PipeType.FLUID -> TextureAtlasManager.pipe_fluid!!
            Network.PipeType.ITEM -> TextureAtlasManager.pipe_item!!
        }
    }

    fun getModelWithTexture(state: IBlockState, model: IBakedModel, texture: TextureAtlasSprite): IBakedModel {
        return SimpleBakedModel.Builder(state, model, texture, BlockPos.ORIGIN).makeBakedModel()
    }
}