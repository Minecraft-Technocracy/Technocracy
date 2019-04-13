package net.cydhra.technocracy.foundation.client.model

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.SimpleBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos


class MachineConnectorBakedModel(val baseBakedModel: IBakedModel, val connector: IBakedModel) : IBakedModel by baseBakedModel {

    companion object {


    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()
        quads.addAll(baseBakedModel.getQuads(state, side, rand))

        val inventory = getModelWithTexture(state!!, connector, TextureAtlasManager.connector_inventory!!)
        val energy = getModelWithTexture(state, connector, TextureAtlasManager.connector_energy!!)

        for ((i, quad) in inventory.getQuads(state, side, rand).withIndex()) {
            //6 faces per cube
            val facing = EnumFacing.values()[i / 6]
            //never render connector on front
            if (facing != EnumFacing.SOUTH)
                quads.add(quad)
        }

        for ((i, quad) in energy.getQuads(state, side, rand).withIndex()) {
            //6 faces per cube
            val facing = EnumFacing.values()[i / 6]
            //never render connector on front
            if (facing == EnumFacing.SOUTH)
                quads.add(quad)
        }

        return quads
    }

    fun getModelWithTexture(state: IBlockState, model: IBakedModel, texture: TextureAtlasSprite): IBakedModel {
        return SimpleBakedModel.Builder(state, model, texture, BlockPos.ORIGIN).makeBakedModel()
    }
}