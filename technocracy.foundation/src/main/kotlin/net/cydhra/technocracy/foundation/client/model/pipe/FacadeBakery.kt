package net.cydhra.technocracy.foundation.client.model.pipe

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad


object FacadeBakery {

    class FakeWorld(var access: IBlockAccess, var fake: IBlockState, var pos: BlockPos) : IBlockAccess by access {
        override fun getBlockState(pos: BlockPos): IBlockState {
            if (pos == this.pos)
                return fake
            return access.getBlockState(pos)
        }
    }

    fun getFacadeQuads(coverFace: EnumFacing, state: IBlockState, pos: BlockPos, faces: BooleanArray): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()

        val mc = Minecraft.getMinecraft()
        val dispatcher = mc.blockRendererDispatcher

        //fix for ctm not working
        val fakeWorld = FakeWorld(mc.world, state, pos)

        var customState = state.getActualState(fakeWorld, pos)
        val coverModel = dispatcher.getModelForState(customState)
        customState = customState.block.getExtendedState(customState, fakeWorld, pos)

        var modquads = coverModel.getQuads(customState, null, 0)
        if (!modquads.isEmpty()) {
            for (quad in modquads) {
                val qm = EdgeCutterConsumer(coverFace, faces, UnpackedBakedQuad.Builder(quad.format))
                qm.facing = null
                qm.setTexture(quad.sprite)
                quad.pipe(qm)
                quads.add(qm.build())
                //quads.add(quad)
            }
        }
        for (side in EnumFacing.values()) {
            modquads = coverModel.getQuads(customState, side, 0)
            if (!modquads.isEmpty()) {
                modquads.forEachIndexed { index, bakedQuad ->
                    val qm = EdgeCutterConsumer(coverFace, faces, UnpackedBakedQuad.Builder(bakedQuad.format))
                    qm.facing = side
                    qm.maxIndex = modquads.size
                    qm.index = index
                    qm.setTexture(bakedQuad.sprite)
                    bakedQuad.pipe(qm)
                    quads.add(qm.build())
                    //quads.add(quad)
                }
            }
        }

        return quads
    }
}