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
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
class RenderProxy {
    private val UNDERWATER_GRAYSCALE = ResourceLocation("technocracy.foundation",
            "textures/extra/underwater_grayscale.png")

    /*@SubscribeEvent
    fun handleFogDensityEvent(event: EntityViewRenderEvent.FogDensity) {
        if (event.entity is EntityPlayer) {
            val player = event.entity as EntityPlayer
            val playerEyePos = RayTracer.getCorrectedHeadVec(player)
            val pos = BlockPos(playerEyePos)
            if (player.world.getBlockState(pos).block is BaseLiquidBlock || player.isPotionActive(oilyEffect)) {
                //event.isCanceled = true
                //GlStateManager.setFog(GlStateManager.FogMode.EXP)
            }
        }
    }


    @SubscribeEvent
    fun handleFogDensityEvent(event: EntityViewRenderEvent.FogColors) {
        if (event.entity is EntityPlayer) {
            val player = event.entity as EntityPlayer
            val playerEyePos = RayTracer.getCorrectedHeadVec(player)
            val pos = BlockPos(playerEyePos)
            if ( player.isPotionActive(oilyEffect)) {
                //event.red = 5/255f
                //event.green = 5/255f
                //event.blue = 5/255f
            }
        }
    }

    @SubscribeEvent
    fun overlay(event: RenderHandEvent) {
        val mc = Minecraft.getMinecraft()
            val player = mc.player

        val playerEyePos = player.getPositionEyes(event.partialTicks)
        val pos = BlockPos(playerEyePos)
        val state = player.world.getBlockState(pos)
        val block = state.block

            if(player.isPotionActive(oilyEffect) && !state.material.isLiquid) {

                event.isCanceled = true
                val flag = mc.renderViewEntity is EntityLivingBase && (mc.renderViewEntity as
                        EntityLivingBase).isPlayerSleeping

                if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI &&
                        !mc.playerController.isSpectator()) {

                    GlStateManager.translate(0.0,0.0,+0.1)
                    mc.entityRenderer.enableLightmap()
                    mc.entityRenderer.itemRenderer.renderItemInFirstPerson(event.partialTicks)
                    mc.entityRenderer.disableLightmap()
                }

                GlStateManager.popMatrix()
                GlStateManager.pushMatrix()

                Minecraft.getMinecraft().renderEngine.bindTexture(UNDERWATER_GRAYSCALE_1)
                GlStateManager.color(0.0f, 0.0f, 0.0f, 1f)
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
        }
    }*/

    @SubscribeEvent
    fun loadWorld(event: WorldEvent.Load) {
        //event.world.provider.skyRenderer = CustomSkyRenderer()
        //event.world.provider = WrappedWorldProvider(event.world.provider)
    }

    @SubscribeEvent
    fun render(event: TickEvent.WorldTickEvent) {
        //if(event.phase == TickEvent.Phase.END) {
        //    event.world.skylightSubtracted = 10
        //}
    }

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
                val brightness = 0.8
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