package net.cydhra.technocracy.foundation.client.renderer.tileEntity

import net.cydhra.technocracy.foundation.tileentity.TileEntityElectricFurnace
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.util.ArrayList
import java.util.HashMap

class TileEntityElectricFurnaceRenderer : TileEntitySpecialRenderer<TileEntityElectricFurnace>() {

    var m = ConnectorModel()

    override fun render(te: TileEntityElectricFurnace, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if(true)
            return
        val mc = Minecraft.getMinecraft()
        val dispatcher = mc.blockRendererDispatcher

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F,  y + 1.5F,  z + 0.5F)
        mc.textureManager.bindTexture(ResourceLocation("technocracy.foundation","textures/block/connector.png"))
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
        m.render(0.0625f)
        GL11.glPopMatrix()
        //dispatcher.blockModelRenderer.renderModel(te.world, dispatcher.getModelForState(te.world.getBlockState(te.pos.add(0,-1,0))), te.world.getBlockState(te.pos.add(0,-1,0)), te.pos.add(0,-1,0), Tessellator.getInstance().buffer, false, 0)
    }
}