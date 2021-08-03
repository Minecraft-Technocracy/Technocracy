package net.cydhra.technocracy.foundation.client.render.tileentity

import net.cydhra.technocracy.foundation.content.tileentities.TileTileWrapper
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity

class TileWrapperBlockSpezialRenderer : TileEntitySpecialRenderer<TileTileWrapper>() {
    override fun render(
        te: TileTileWrapper,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        val tile = te.wrappedTile ?: return
        TileEntityRendererDispatcher.instance.getRenderer<TileEntity>(tile)!!
            .render(tile, x, y, z, partialTicks, destroyStage, alpha)
    }
}