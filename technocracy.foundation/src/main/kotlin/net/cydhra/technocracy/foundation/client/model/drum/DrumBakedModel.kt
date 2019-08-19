package net.cydhra.technocracy.foundation.client.model.drum

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BakedQuadRetextured
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.model.ModelDynBucket
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fluids.FluidRegistry


class DrumBakedModel(val baseBakedModel: IBakedModel, val cover: IBakedModel) : IBakedModel by baseBakedModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        val bucket = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(ResourceLocation(ForgeVersion.MOD_ID, "items/bucket_cover").toString())
        val texture = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(FluidRegistry.WATER.still.toString())

        val map = mutableListOf<BakedQuad>()
        val cutout = MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT

        if (cutout) {
            map.addAll(baseBakedModel.getQuads(state, side, rand))
        }
        for (quad in cover.getQuads(state, side, rand)) {

            val tex = if (quad.sprite.iconName == "missingno") {
                if (cutout) texture else continue
            } else if (!cutout) quad.sprite else continue

            map.add(BakedQuadRetextured(quad, tex))
        }


        return map
    }
}