package net.cydhra.technocracy.megastructures.client.renderer

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object CustomSkyRenderer : IRenderHandler() {

    override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
        renderSky(partialTicks, world, mc)
    }

    @SubscribeEvent
    fun modifyFogColor(e: EntityViewRenderEvent.FogColors) {
        e.red * 0.2
        e.green * 0.2
        e.blue * 0.2
    }

    private val MOON_PHASES_TEXTURES = ResourceLocation("textures/environment/moon_phases.png")
    private val SUN_TEXTURES = ResourceLocation("textures/environment/sun.png")
    private val CLOUDS_TEXTURES = ResourceLocation("textures/environment/clouds.png")
    private val END_SKY_TEXTURES = ResourceLocation("textures/environment/end_sky.png")
    private val FORCEFIELD_TEXTURES = ResourceLocation("textures/misc/forcefield.png")

    fun renderSky(partialTicks: Float, world: WorldClient, mc: Minecraft) {

        val pass = 2
        val renderGlobal = mc.renderGlobal

        if (mc.world.provider.dimensionType.id == 1) {
            renderGlobal.renderSkyEnd()
        } else if (mc.world.provider.isSurfaceWorld) {
            GlStateManager.disableTexture2D()
            val vec3d = world.getSkyColor(mc.renderViewEntity!!, partialTicks)
            var f = vec3d.x.toFloat() * 0.2f
            var f1 = vec3d.y.toFloat() * 0.2f
            var f2 = vec3d.z.toFloat() * 0.2f

            //TODO modify saturation

            if (pass != 2) {
                val f3 = (f * 30.0f + f1 * 59.0f + f2 * 11.0f) / 100.0f
                val f4 = (f * 30.0f + f1 * 70.0f) / 100.0f
                val f5 = (f * 30.0f + f2 * 70.0f) / 100.0f
                f = f3
                f1 = f4
                f2 = f5
            }

            GlStateManager.color(f, f1, f2)
            val tessellator = Tessellator.getInstance()
            val bufferBuilder = tessellator.buffer
            GlStateManager.depthMask(false)
            GlStateManager.enableFog()
            GlStateManager.color(f, f1, f2)

            if (renderGlobal.vboEnabled) {
                renderGlobal.skyVBO.bindBuffer()
                GlStateManager.glEnableClientState(32884)
                GlStateManager.glVertexPointer(3, 5126, 12, 0)
                renderGlobal.skyVBO.drawArrays(7)
                renderGlobal.skyVBO.unbindBuffer()
                GlStateManager.glDisableClientState(32884)
            } else {
                GlStateManager.callList(renderGlobal.glSkyList)
            }

            GlStateManager.disableFog()
            GlStateManager.disableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            RenderHelper.disableStandardItemLighting()
            val afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks)

            if (afloat != null) {
                GlStateManager.disableTexture2D()
                GlStateManager.shadeModel(7425)
                GlStateManager.pushMatrix()
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(if (MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0f) 180.0f else 0.0f, 0.0f, 0.0f, 1.0f)
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f)
                var f6 = afloat[0] * 0.2f
                var f7 = afloat[1] * 0.2f
                var f8 = afloat[2] * 0.2f

                //TODO modify saturation
                if (pass != 2) {
                    val f9 = (f6 * 30.0f + f7 * 59.0f + f8 * 11.0f) / 100.0f
                    val f10 = (f6 * 30.0f + f7 * 70.0f) / 100.0f
                    val f11 = (f6 * 30.0f + f8 * 70.0f) / 100.0f
                    f6 = f9
                    f7 = f10
                    f8 = f11
                }

                bufferBuilder.begin(6, DefaultVertexFormats.POSITION_COLOR)
                bufferBuilder.pos(0.0, 100.0, 0.0).color(f6, f7, f8, afloat[3]).endVertex()

                for (j2 in 0..16) {
                    val f21 = j2.toFloat() * (Math.PI.toFloat() * 2f) / 16.0f
                    val f12 = MathHelper.sin(f21)
                    val f13 = MathHelper.cos(f21)
                    bufferBuilder.pos((f12 * 120.0f).toDouble(), (f13 * 120.0f).toDouble(), (-f13 * 40.0f * afloat[3]).toDouble()).color(afloat[0], afloat[1], afloat[2], 0.0f).endVertex()
                }

                tessellator.draw()
                GlStateManager.popMatrix()
                GlStateManager.shadeModel(7424)
            }

            GlStateManager.enableTexture2D()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.pushMatrix()
            val f16 = 1.0f - world.getRainStrength(partialTicks)
            GlStateManager.color(1.0f, 1.0f, 1.0f, f16)
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
            var f17 = 30.0f
            mc.renderEngine.bindTexture(SUN_TEXTURES)
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferBuilder.pos((-f17).toDouble(), 100.0, (-f17).toDouble()).tex(0.0, 0.0).endVertex()
            bufferBuilder.pos(f17.toDouble(), 100.0, (-f17).toDouble()).tex(1.0, 0.0).endVertex()
            bufferBuilder.pos(f17.toDouble(), 100.0, f17.toDouble()).tex(1.0, 1.0).endVertex()
            bufferBuilder.pos((-f17).toDouble(), 100.0, f17.toDouble()).tex(0.0, 1.0).endVertex()
            tessellator.draw()
            f17 = 20.0f
            mc.renderEngine.bindTexture(MOON_PHASES_TEXTURES)
            val k1 = world.moonPhase
            val i2 = k1 % 4
            val k2 = k1 / 4 % 2
            val f22 = (i2 + 0).toFloat() / 4.0f
            val f23 = (k2 + 0).toFloat() / 2.0f
            val f24 = (i2 + 1).toFloat() / 4.0f
            val f14 = (k2 + 1).toFloat() / 2.0f
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferBuilder.pos((-f17).toDouble(), -100.0, f17.toDouble()).tex(f24.toDouble(), f14.toDouble()).endVertex()
            bufferBuilder.pos(f17.toDouble(), -100.0, f17.toDouble()).tex(f22.toDouble(), f14.toDouble()).endVertex()
            bufferBuilder.pos(f17.toDouble(), -100.0, (-f17).toDouble()).tex(f22.toDouble(), f23.toDouble()).endVertex()
            bufferBuilder.pos((-f17).toDouble(), -100.0, (-f17).toDouble()).tex(f24.toDouble(), f23.toDouble()).endVertex()
            tessellator.draw()
            GlStateManager.disableTexture2D()
            val f15 = world.getStarBrightness(partialTicks) * f16

            if (f15 > 0.0f) {
                GlStateManager.color(f15, f15, f15, f15)

                if (renderGlobal.vboEnabled) {
                    renderGlobal.starVBO.bindBuffer()
                    GlStateManager.glEnableClientState(32884)
                    GlStateManager.glVertexPointer(3, 5126, 12, 0)
                    renderGlobal.starVBO.drawArrays(7)
                    renderGlobal.starVBO.unbindBuffer()
                    GlStateManager.glDisableClientState(32884)
                } else {
                    GlStateManager.callList(renderGlobal.starGLCallList)
                }
            }

            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.enableFog()
            GlStateManager.popMatrix()
            GlStateManager.disableTexture2D()
            GlStateManager.color(0.0f, 0.0f, 0.0f)
            val d3 = mc.player.getPositionEyes(partialTicks).y - world.horizon

            if (d3 < 0.0) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(0.0f, 12.0f, 0.0f)

                if (renderGlobal.vboEnabled) {
                    renderGlobal.sky2VBO.bindBuffer()
                    GlStateManager.glEnableClientState(32884)
                    GlStateManager.glVertexPointer(3, 5126, 12, 0)
                    renderGlobal.sky2VBO.drawArrays(7)
                    renderGlobal.sky2VBO.unbindBuffer()
                    GlStateManager.glDisableClientState(32884)
                } else {
                    GlStateManager.callList(renderGlobal.glSkyList2)
                }

                GlStateManager.popMatrix()
                val f19 = -(d3 + 65.0).toFloat()
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
                bufferBuilder.pos(-1.0, f19.toDouble(), 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, f19.toDouble(), 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, f19.toDouble(), -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, f19.toDouble(), -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, f19.toDouble(), 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, f19.toDouble(), -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, f19.toDouble(), -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, f19.toDouble(), 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex()
                bufferBuilder.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex()
                tessellator.draw()
            }

            if (world.provider.isSkyColored) {
                GlStateManager.color(f * 0.2f + 0.04f, f1 * 0.2f + 0.04f, f2 * 0.6f + 0.1f)
            } else {
                GlStateManager.color(f, f1, f2)
            }

            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0f, -(d3 - 16.0).toFloat(), 0.0f)
            GlStateManager.callList(renderGlobal.glSkyList2)
            GlStateManager.popMatrix()
            GlStateManager.enableTexture2D()
            GlStateManager.depthMask(true)
        }
    }
}