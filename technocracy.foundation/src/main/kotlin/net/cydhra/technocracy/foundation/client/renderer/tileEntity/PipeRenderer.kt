package net.cydhra.technocracy.foundation.client.renderer.tileEntity

import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer


class PipeRenderer : TileEntitySpecialRenderer<TileEntityPipe>() {

    override fun render(te: TileEntityPipe, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        this.setLightmapDisabled(true)
        this.drawNameplate(te, te.getNetworkId().toString(), x, y, z, 12)
        this.setLightmapDisabled(false)
    }
}