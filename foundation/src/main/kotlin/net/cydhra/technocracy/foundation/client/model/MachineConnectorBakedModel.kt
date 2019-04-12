package net.cydhra.technocracy.foundation.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.util.EnumFacing


class MachineConnectorBakedModel(val baseBakedModel: IBakedModel, val connector: IBakedModel) : IBakedModel by baseBakedModel {

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()
        quads.addAll(baseBakedModel.getQuads(state, side, rand))

        for ((i, quad) in connector.getQuads(state, side, rand).withIndex()) {
            //6 faces per cube
            val facing = EnumFacing.values()[i / 6]
            //never render connector on front
            if (facing != EnumFacing.SOUTH)
                quads.add(quad)
        }

        return quads
    }
}