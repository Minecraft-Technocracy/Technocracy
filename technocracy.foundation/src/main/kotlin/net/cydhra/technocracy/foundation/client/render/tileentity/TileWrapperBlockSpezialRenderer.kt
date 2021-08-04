package net.cydhra.technocracy.foundation.client.render.tileentity

import net.cydhra.technocracy.foundation.content.tileentities.TileWrapperTileEntity
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity

class TileWrapperBlockSpezialRenderer : TileEntitySpecialRenderer<TileWrapperTileEntity>() {
    override fun render(
        te: TileWrapperTileEntity,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        val tile = te.getWrappedTile() ?: return
        tile.world = te.getWorld(world)
        TileEntityRendererDispatcher.instance.getRenderer<TileEntity>(tile)
            ?.render(tile, x, y, z, partialTicks, destroyStage, alpha)
    }
}