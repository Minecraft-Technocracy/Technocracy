package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderBlockOverlayEvent
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
class RenderProxy {
    private val UNDERWATER_GRAYSCALE = ResourceLocation("technocracy.foundation", "extra/underwater_grayscale")

    @SubscribeEvent
    @Suppress("unused")
    fun handleFluidBlockOverlayEvent(event: RenderBlockOverlayEvent) {
        //Cofh copy pasta
        if (event.overlayType == RenderBlockOverlayEvent.OverlayType.WATER) {
            val player = event.player
            val playerEyePos = player.getPositionEyes(event.renderPartialTicks)
            val pos = BlockPos(playerEyePos)
            val state = player.world.getBlockState(pos)
            val block = state.block

            if (block is BlockFluidBase) {
                Minecraft.getMinecraft().renderEngine.bindTexture(UNDERWATER_GRAYSCALE)
                val brightness = player.brightness.toDouble()
                val color = block.getFogColor(player.world, pos, state, player, Vec3d(1.0, 1.0, 1.0), 0.0f).scale(brightness)

                GlStateManager.color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), 0.4f)
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
                GlStateManager.pushMatrix()

                val yaw = -player.rotationYaw / 64.0f
                val pitch = player.rotationPitch / 64.0f

                val t = Tessellator.getInstance()
                val buffer = t.buffer

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX)
                buffer.pos(-1.0, -1.0, -0.5).tex((4.0f + yaw).toDouble(), (4.0f + pitch).toDouble()).endVertex()
                buffer.pos(1.0, -1.0, -0.5).tex((0.0f + yaw).toDouble(), (4.0f + pitch).toDouble()).endVertex()
                buffer.pos(1.0, 1.0, -0.5).tex((0.0f + yaw).toDouble(), (0.0f + pitch).toDouble()).endVertex()
                buffer.pos(-1.0, 1.0, -0.5).tex((4.0f + yaw).toDouble(), (0.0f + pitch).toDouble()).endVertex()
                t.draw()

                GlStateManager.popMatrix()
                GlStateManager.disableBlend()
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                event.isCanceled = true
            }
        }
    }
}