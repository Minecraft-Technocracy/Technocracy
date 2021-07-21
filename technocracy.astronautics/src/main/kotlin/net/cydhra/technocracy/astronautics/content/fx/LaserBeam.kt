package net.cydhra.technocracy.astronautics.content.fx

import net.cydhra.technocracy.coremod.event.RenderShaderEvent
import net.cydhra.technocracy.foundation.api.fx.AbstractParticle
import net.cydhra.technocracy.foundation.api.fx.IParticleType
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.MultiTargetFBO
import net.cydhra.technocracy.foundation.util.opengl.VBO
import net.cydhra.technocracy.foundation.util.validate
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.util.stream.Stream
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt


class LaserBeam(worldIn: World, posXIn: Double, posYIn: Double, posZIn: Double) : AbstractParticle(worldIn, posXIn, posYIn, posZIn) {

    init {
        particleAge = 20 * 10
    }

    override fun getType(): IParticleType {
        return ParticleLaserBeamType
    }

    override fun renderParticle(partialTicks: Float) {
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)

        val posX = getX(partialTicks)
        val posY = getY(partialTicks).toDouble()
        val posZ = getZ(partialTicks)

        var bol = false

        val step = 360 / 4.0

        for (i in 1 until 4) {
            val offx = -sin(Math.toRadians(step * i.toDouble()))
            val offz = cos(Math.toRadians(step * i.toDouble()))

            buffer.pos(posX + offx, posY, posZ + offz).endVertex()
            buffer.pos(posX + offx, posY + 1, posZ + offz).endVertex()
            bol = !bol
        }

        tess.draw()
    }

    override fun move(x: Double, y: Double, z: Double) {
        isExpired = false
        particleAge++
        renderTime += 0.0005f
    }

    object ParticleLaserBeamType : IParticleType {
        override val name = "LaserBeam"
        override val perParticleRender = false
        override val maxParticles = -1

        override val mutex = null

        private val sqr_2half = sqrt(2.toDouble()) * 0.5

        lateinit var inner: VBO

        var sizeOuter = -1
        var init = false

        var mtfbo: MultiTargetFBO? = null
        var pingPong: MultiTargetFBO? = null
        var ping: Framebuffer? = null
        var pong: Framebuffer? = null
        var downSampleFB: Framebuffer? = null

        val gaus = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/default.vsh"), ResourceLocation("technocracy.astronautics", "shaders/gaus.fsh"))
        val horizontal = gaus.getUniform("horizontal", BasicShaderProgram.ShaderUniform.UniformType.INT_1)
        val expandFaktor = gaus.getUniform("expandFaktor", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1f)

        val kawase = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/default.vsh"), ResourceLocation("technocracy.astronautics", "shaders/kawase.fsh"))
        val u_xyPixelSize_zIteration = kawase.getUniform("u_xyPixelSize_zIteration", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3)


        val downSample = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/default.vsh"), ResourceLocation("technocracy.astronautics", "shaders/downsample.fsh")) {
            getUniform("image", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
        }

        val pixelSize = downSample.getUniform("pixelSize", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)

        val blend = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/default.vsh"), ResourceLocation("technocracy.astronautics", "shaders/blend.fsh")) {
            getUniform("scene", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
            getUniform("bloomBlur", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(2)
        }
        val combines = blend.getUniform("combines", BasicShaderProgram.ShaderUniform.UniformType.INT_1)
        val gamma = blend.getUniform("gamma", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
        val uApplyParams = blend.getUniform("uApplyParams", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)
        val finalScreenSize = blend.getUniform("finalScreenSize", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)

        val laser = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shaders/laser.vsh"), ResourceLocation("technocracy.astronautics", "shaders/laser.fsh"))
        val center = laser.getUniform("center", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3)
        val time = laser.getUniform("time", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
        val scale = laser.getUniform("scale", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
        val spread = laser.getUniform("spread", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
        val rotateScale = laser.getUniform("rotateScale", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)

        var renderedParticle = false

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        override fun preRenderType() {
        }

        var rebuild = false

        override fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {
            renderedParticle = true

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.disableTexture2D()
            GlStateManager.depthMask(false)
            GlStateManager.disableAlpha()

            val size = max(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 * 16 / 10, 80)

            if (!init) {
                init = true

                val floats = mutableListOf<Float>()
                generateNoodle(10, 5f, size, 0f, floats)
                sizeOuter = floats.size
                generateNoodle(5, 2f, size, 0f, floats)
                inner = VBO(VBO.VBOUsage.STATIC_DRAW, floats.toFloatArray()).addFloatAttribute(3)
            }


            //TODO fix laser beeing rendered over the hand
            //attempt to fix rendering over the hand, by rendering the hand to the depth buffer
            //breakes because it the hand is bigger then what is visible, would need a shader pass to fix it
            /*val mc = Minecraft.getMinecraft()

            GlStateManager.pushMatrix()


            GlStateManager.matrixMode(5889)
            GlStateManager.loadIdentity()

            Project.gluPerspective(getFOVModifier(partialTicks, false), mc.displayWidth.toFloat() / mc.displayHeight, 0.05f, mc.gameSettings.renderDistanceChunks * 16f * 2.0f)
            GlStateManager.matrixMode(5888)
            GlStateManager.loadIdentity()

            GlStateManager.depthMask(true)
            GlStateManager.colorMask(false, false, false, false)

            val renderView = mc.renderViewEntity

            if (mc.gameSettings.viewBobbing) {
                if (renderView is EntityPlayer) {
                    val f = renderView.distanceWalkedModified - renderView.prevDistanceWalkedModified
                    val f1 = -(renderView.distanceWalkedModified + f * partialTicks)
                    val f2 = renderView.prevCameraYaw + (renderView.cameraYaw - renderView.prevCameraYaw) * partialTicks
                    val f3 = renderView.prevCameraPitch + (renderView.cameraPitch - renderView.prevCameraPitch) * partialTicks
                    GlStateManager.translate(MathHelper.sin(f1 * Math.PI.toFloat()) * f2 * 0.5f, -abs(MathHelper.cos(f1 * Math.PI.toFloat()) * f2), 0.0f)
                    GlStateManager.rotate(MathHelper.sin(f1 * Math.PI.toFloat()) * f2 * 3.0f, 0.0f, 0.0f, 1.0f)
                    GlStateManager.rotate(abs(MathHelper.cos(f1 * Math.PI.toFloat() - 0.2f) * f2) * 5.0f, 1.0f, 0.0f, 0.0f)
                    GlStateManager.rotate(f3, 1.0f, 0.0f, 0.0f)
                }
            }

            val flag = renderView is EntityLivingBase && renderView.isPlayerSleeping

            if (!ForgeHooksClient.renderFirstPersonHand(mc.renderGlobal, partialTicks, 2)) if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
                mc.entityRenderer.itemRenderer.renderItemInFirstPerson(partialTicks)
            }

            RenderHelper.enableStandardItemLighting()


            GlStateManager.colorMask(true, true, true, true)

            GlStateManager.popMatrix()*/


            mtfbo = mtfbo.validate(Minecraft.getMinecraft().framebuffer, hdrFrameBuffer = false)
            val mtfbo = mtfbo!!
            mtfbo.framebufferClear()
            mtfbo.bindFramebuffer(false)

            if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
                if (rebuild) {
                    rebuild = false
                    laser.reloadShader(Minecraft.getMinecraft().resourceManager)
                    downSample.reloadShader(Minecraft.getMinecraft().resourceManager)
                    kawase.reloadShader(Minecraft.getMinecraft().resourceManager)
                    blend.reloadShader(Minecraft.getMinecraft().resourceManager)
                }
            } else {
                rebuild = true
            }

            laser.start()

            for (first in particles) {
                val posX = first.getX(partialTicks).toDouble()
                val posY = first.getY(partialTicks).toDouble()
                val posZ = first.getZ(partialTicks).toDouble()

                GlStateManager.translate(posX, posY, posZ)

                time.uploadUniform((first.getAge() + partialTicks) / 3.0)
                scale.uploadUniform(0.5f)
                spread.uploadUniform(3f)

                center.uploadUniform(0.0, 0.0, 0.0)
                rotateScale.uploadUniform(0f)
                laser.updateUniforms()

                inner.bindVBO()

                GlStateManager.color(0.3f, 0.4f, 0.3f, 0.1f)

                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, sizeOuter / 3)

                time.uploadUniform((first.getAge() + partialTicks) / 2.0)
                scale.uploadUniform(0.2f)
                spread.uploadUniform(4f)
                laser.updateUniforms()

                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, sizeOuter / 3)


                time.uploadUniform((first.getAge() + partialTicks))
                scale.uploadUniform(0.0f)
                rotateScale.uploadUniform(15f)
                laser.updateUniforms()

                GlStateManager.color(1f, 0.7f, 0.6f, 0.6f)
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, sizeOuter / 3, inner.size / 3 - 1)

                inner.unbindVBO()

                GlStateManager.translate(-posX, -posY, -posZ)

                /*

                var shape = 7
                var step = 360 / (shape.toDouble() - 1)
                var baseSize = 5f
                var scaling = baseSize / 300f


                val ringOffset = 2f
                //Minecraft.getMinecraft().entityRenderer.enableLightmap()
                //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 140f, 140f)


                shape = 7
                step = 360 / (shape.toDouble() - 1)
                baseSize = 5f
                scaling = baseSize / 300f

                if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
                    if (rebuild) {
                        rebuild = false
                        laser.reloadShader(Minecraft.getMinecraft().resourceManager)
                    }
                } else {
                    rebuild = true
                }

                laser.start()
                time.uploadUniform((first.getAge() + partialTicks) / 3.0)
                scale.uploadUniform(0.5f)
                spread.uploadUniform(3f)
                center.uploadUniform(posX, Minecraft.getMinecraft().renderManager.viewerPosY, posZ)
                laser.updateUniforms()

                GlStateManager.color(0.8f, 0.1f, 0.8f, 0.1f)
                //drawWobbleNoodle(posX, posY, posZ, 10, baseSize, size, first.getAge() * 8f + partialTicks, 0.2f)

                GlStateManager.color(0.4f, 0.5f, 0.4f, 0.1f)
                //drawWobbleNoodle(posX, posY, posZ, 10, baseSize - 2, size, first.getAge() * 7f + partialTicks, 0f)

                GlStateManager.color(0.3f, 0.4f, 0.3f, 0.1f)
                drawWobbleNoodle(posX, posY, posZ, 10, baseSize - 1.3f, size, /*first.getAge() * 8f + partialTicks*/8f, 0f)

                GlStateManager.color(0.2f, 0.3f, 0.2f, 0.25f)

                time.uploadUniform((first.getAge() + partialTicks) / 5.0)
                laser.updateUniforms()

                drawWobbleNoodle(posX, posY, posZ, 10, baseSize - 0.8f, size, /*first.getAge() * 10f + partialTicks*/ 10f, 0f)
                laser.stop()

                /*buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)

            for (y in 0..300) {
                val rnd = Random(y)
                val size = baseSize - sin(Math.toRadians((36.0 + rnd.nextInt(2) + rnd.nextDouble()) * y + first.getAge() * 8)) * 0.3 - scaling * y

                val nextRnd = Random(y + 1)
                //size of the next ring
                val sizeNext = baseSize - sin(Math.toRadians((36.0 + nextRnd.nextInt(2) + nextRnd.nextDouble()) * (y + 1) + first.getAge() * 8)) * 0.3 - scaling * (y + 1)

                val startOffX = sin(Math.toRadians(step + 45)) * sqr_2half
                val startOffZ = -cos(Math.toRadians(step + 45)) * sqr_2half

                //draws a invisible triangle with an area of 0 connecting the rings
                //current ring
                if (y != 0 && ringOffset != 0f)
                    buffer.pos(posX + startOffX * size, posY + y + y * 2, posZ + startOffZ * size).color(0.8f, 0.1f, 0.8f, 0.3f).endVertex()

                for (i in 1..shape) {
                    val offx = sin(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half
                    val offz = -cos(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half

                    buffer.pos(posX + offx * size, posY + y + y * 2, posZ + offz * size).color(0.8f, 0.1f, 0.8f, 0.3f).endVertex()
                    buffer.pos(posX + offx * sizeNext, posY + 1 + y + y * 2, posZ + offz * sizeNext).color(0.8f, 0.1f, 0.8f, 0.3f).endVertex()
                }

                //draws a invisible triangle with an area of 0 connecting the rings
                //upper ring
                if (ringOffset != 0f)
                    buffer.pos(posX + startOffX * sizeNext, posY + 1 + y + y * 2, posZ + startOffZ * sizeNext).color(0.8f, 0.1f, 0.8f, 0.3f).endVertex()
            }
            tess.draw()*/

                shape = 5
                step = 360 / shape.toDouble()
                baseSize = 2f
                scaling = baseSize / size.toFloat()

                val rotTime = first.getAge() + partialTicks

                GlStateManager.color(172 / 255f, 127 / 255f, 255 / 255f, 0.6f)
                //GlStateManager.color(0.698f, 0.643f, 0.925f, 0.2f)
                GlStateManager.color(1f, 0.7f, 0.6f, 0.6f)

                //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)


                buffer.begin(GL11.GL_TRIANGLE_STRIP, POSITION)

                val rotationOffset = 10.0
                val rotationOffsetRadians = Math.toRadians(rotationOffset)

                for (y in 0 until size) {

                    val size = baseSize - scaling * y

                    for (i in 0..shape) {
                        val currStep = step * i.toDouble() + 45 + rotTime + y * rotationOffset
                        val rad = Math.toRadians(currStep)

                        val offx = sin(rad) * sqr_2half
                        val offz = -cos(rad) * sqr_2half

                        val offxNext = sin(rad + rotationOffsetRadians) * sqr_2half
                        val offzNext = -cos(rad + rotationOffsetRadians) * sqr_2half

                        //lower vertex
                        buffer.pos(posX + offx * size, posY + y, posZ + offz * size).endVertex()
                        //upper vertex with offsets of next ring
                        buffer.pos(posX + offxNext * (size - scaling), posY + y + 1, posZ + offzNext * (size - scaling)).endVertex()
                    }
                }
                tess.draw()*/
            }

            laser.stop()

            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.depthMask(true)
            GlStateManager.enableCull()

            return 0
        }

        private fun getFOVModifier(partialTicks: Float, useFOVSetting: Boolean): Float {

            val mc = Minecraft.getMinecraft()

            val entity = mc.getRenderViewEntity()
            var f = 70.0f
            if (entity is EntityLivingBase && entity.health <= 0.0f) {
                val f1 = entity.deathTime.toFloat() + partialTicks
                f /= (1.0f - 500.0f / (f1 + 500.0f)) * 2.0f + 1.0f
            }
            val iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partialTicks)
            if (iblockstate.material === Material.WATER) {
                f = f * 60.0f / 70.0f
            }
            return f
        }

        @SubscribeEvent
        fun draw(overlay: RenderShaderEvent) {
            if (!renderedParticle)
                return

            Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();

            renderedParticle = false

            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val sW = scaledResolution.getScaledWidth_double()
            val sH = scaledResolution.getScaledHeight_double()

            val downsampledRTFactor = 4
            val dsw: Int = (sW + downsampledRTFactor - 1).toInt() / downsampledRTFactor
            val dsh: Int = (sH + downsampledRTFactor - 1).toInt() / downsampledRTFactor

            ping = ping.validateAndClear()
            pong = pong.validateAndClear()
            downSampleFB = downSampleFB.validateAndClear(dsw, dsh)
            val ping = ping!!
            val pong = pong!!
            val downSampleFB = downSampleFB!!

            var horizontal = true

            GlStateManager.bindTexture(mtfbo!!.textureTwo)

            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.disableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

            val tess = Tessellator.getInstance()
            val buffer = tess.buffer

            /*gaus!!.start()
            for (i in 0..4) {

                this.horizontal.uploadUniform(horizontal)

                expand.uploadUniform(1)

                expandFaktor.uploadUniform(i + 1)
                gaus!!.updateUniforms()

                if (horizontal) {
                    TCOpenGlHelper.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT1)
                } else {
                    TCOpenGlHelper.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0)
                }

                buffer.begin(GL11.GL_QUADS, POSITION_TEX)
                buffer.pos(0.0, 0.0, 1.0).tex(0.0, 1.0).endVertex()
                buffer.pos(0.0, sH, 1.0).tex(0.0, 0.0).endVertex()
                buffer.pos(sW, sH, 1.0).tex(1.0, 0.0).endVertex()
                buffer.pos(sW, 0.0, 0.0).tex(1.0, 1.0).endVertex()
                tess.draw()

                GlStateManager.bindTexture(if (!horizontal) pingPong.textureOne else pingPong.textureTwo)

                horizontal = !horizontal
            }*/

            //val kernel = intArrayOf(0, 1, 2, 3, 4, 5, 7, 8, 9, 10)

            var blur = 2f
            val quality = 4f
            val step = blur / quality


            //region downsampler
            downSample.start()

            pixelSize.uploadUniform(1f / dsw, 1f / dsh)
            downSample.updateUniforms()

            downSampleFB.bindFramebuffer(true)

            buffer.begin(GL11.GL_QUADS, POSITION_TEX)
            buffer.pos(0.0, 0.0, 1.0).tex(0.0, 1.0).endVertex()
            buffer.pos(0.0, sH, 1.0).tex(0.0, 0.0).endVertex()
            buffer.pos(sW, sH, 1.0).tex(1.0, 0.0).endVertex()
            buffer.pos(sW, 0.0, 0.0).tex(1.0, 1.0).endVertex()
            tess.draw()

            GlStateManager.bindTexture(downSampleFB.framebufferTexture)

            downSample.stop()
            //endregion


            kawase.start()

            for (i in 0..quality.toInt()) {
                u_xyPixelSize_zIteration.uploadUniform(1f / sW.toInt(), 1f / sH.toInt(), blur)
                blur -= step
                kawase.updateUniforms()

                if (horizontal) {
                    ping.bindFramebuffer(true)
                } else {
                    pong.bindFramebuffer(true)
                }

                buffer.begin(GL11.GL_QUADS, POSITION_TEX)
                buffer.pos(0.0, 0.0, 1.0).tex(0.0, 1.0).endVertex()
                buffer.pos(0.0, sH, 1.0).tex(0.0, 0.0).endVertex()
                buffer.pos(sW, sH, 1.0).tex(1.0, 0.0).endVertex()
                buffer.pos(sW, 0.0, 0.0).tex(1.0, 1.0).endVertex()
                tess.draw()

                GlStateManager.bindTexture(if (horizontal) ping.framebufferTexture else pong.framebufferTexture)

                horizontal = !horizontal
            }

            /*
                        const int shaderKernel[] = { 0, 1, 2, 3, 4, 5, 7, 8, 9, 10 };
            mSettings.KawaseBlurPasses = _countof( shaderKernel );
             */

            kawase.stop()

            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

            blend.start()
            combines.uploadUniform(2)
            gamma.uploadUniform(1.1f)

            finalScreenSize.uploadUniform(1f / sW, 1f / sH)
            uApplyParams.uploadUniform(10f, 0.6f - 4 * 0.04f)


            blend.updateUniforms()

            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
            GlStateManager.bindTexture(if (!horizontal) ping.framebufferTexture else pong.framebufferTexture)
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            GlStateManager.bindTexture(Minecraft.getMinecraft().framebuffer.framebufferTexture)

            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2d(0.0, 1.0)
            GL11.glVertex2d(0.0, 0.0)
            GL11.glTexCoord2d(0.0, 0.0)
            GL11.glVertex2d(0.0, sH)
            GL11.glTexCoord2d(1.0, 0.0)
            GL11.glVertex2d(sW, sH)
            GL11.glTexCoord2d(1.0, 1.0)
            GL11.glVertex2d(sW, 0.0)
            GL11.glEnd()

            blend.stop()

            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
            GlStateManager.bindTexture(0)
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            GlStateManager.bindTexture(0)

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

            GlStateManager.enableAlpha()
        }

        fun generateNoodle(shape: Int, sizeXZ: Float, sizeY: Int, ringOffset: Float, floats: MutableList<Float>) {

            val height = sizeY.toDouble()

            val step = 360 / shape.toDouble()
            val scaling = sizeXZ / height

            for (y in 0 until sizeY) {
                if (y + y * ringOffset >= sizeY)
                    break
                //val rnd = Random(y)
                val size = sizeXZ - scaling * y

                //val nextRnd = Random(y + 1)
                //size of the next ring
                val sizeNext = sizeXZ - scaling * (y + 1)

                val startOffX = sin(Math.toRadians(step + 45)) * sqr_2half
                val startOffZ = -cos(Math.toRadians(step + 45)) * sqr_2half

                //draws a invisible triangle with an area of 0 connecting the rings
                //current ring
                if (y != 0 && ringOffset != 0f) {
                    floats.add((startOffX * size).toFloat())
                    floats.add(y + y * ringOffset)
                    floats.add((startOffZ * size).toFloat())
                }

                for (i in 1..(shape + 1)) {
                    val offx = sin(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half
                    val offz = -cos(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half

                    floats.add((offx * size).toFloat())
                    floats.add(y + y * ringOffset)
                    floats.add((offz * size).toFloat())

                    floats.add((offx * sizeNext).toFloat())
                    floats.add(1 + y + y * ringOffset)
                    floats.add((offz * sizeNext).toFloat())
                }

                //draws a invisible triangle with an area of 0 connecting the rings
                //upper ring
                if (ringOffset != 0f) {
                    floats.add((startOffX * sizeNext).toFloat())
                    floats.add(1 + y + y * ringOffset)
                    floats.add((startOffZ * sizeNext).toFloat())
                }
            }
        }

        fun drawWobbleNoodle(posX: Double, posY: Double, posZ: Double, shape: Int, sizeXZ: Float, sizeY: Int, time: Float, ringOffset: Float) {
            val tess = Tessellator.getInstance()
            val buffer = tess.buffer


            val height = sizeY.toDouble()

            val step = 360 / shape.toDouble()
            val scaling = sizeXZ / height

            buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)

            for (y in 0 until sizeY) {
                if (y + y * ringOffset >= sizeY)
                    break
                //val rnd = Random(y)
                val size = sizeXZ - scaling * y

                //val nextRnd = Random(y + 1)
                //size of the next ring
                val sizeNext = sizeXZ - scaling * (y + 1)

                val startOffX = sin(Math.toRadians(step + 45)) * sqr_2half
                val startOffZ = -cos(Math.toRadians(step + 45)) * sqr_2half

                //draws a invisible triangle with an area of 0 connecting the rings
                //current ring
                if (y != 0 && ringOffset != 0f)
                    buffer.pos(posX + startOffX * size, posY + y + y * ringOffset, posZ + startOffZ * size).endVertex()

                for (i in 1..(shape + 1)) {
                    val offx = sin(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half
                    val offz = -cos(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half

                    buffer.pos(posX + offx * size, posY + y + y * ringOffset, posZ + offz * size).endVertex()
                    buffer.pos(posX + offx * sizeNext, posY + 1 + y + y * ringOffset, posZ + offz * sizeNext).endVertex()
                }

                //draws a invisible triangle with an area of 0 connecting the rings
                //upper ring
                if (ringOffset != 0f)
                    buffer.pos(posX + startOffX * sizeNext, posY + 1 + y + y * ringOffset, posZ + startOffZ * sizeNext).endVertex()
            }

            /*for (y in 0 until sizeY) {
                if (y + y * ringOffset >= sizeY)
                    break
                //val rnd = Random(y)
                val size = sizeXZ - sin(Math.toRadians((36.0 /*+ rnd.nextInt(2) + rnd.nextDouble()*/) * y + time)) * 0.3 - scaling * y

                //val nextRnd = Random(y + 1)
                //size of the next ring
                val sizeNext = sizeXZ - sin(Math.toRadians((36.0/* + nextRnd.nextInt(2) + nextRnd.nextDouble()*/) * (y + 1) + time)) * 0.3 - scaling * (y + 1)

                val startOffX = sin(Math.toRadians(step + 45)) * sqr_2half
                val startOffZ = -cos(Math.toRadians(step + 45)) * sqr_2half

                //draws a invisible triangle with an area of 0 connecting the rings
                //current ring
                if (y != 0 && ringOffset != 0f)
                    buffer.pos(posX + startOffX * size, posY + y + y * ringOffset, posZ + startOffZ * size).endVertex()

                for (i in 1..(shape + 1)) {
                    val offx = sin(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half
                    val offz = -cos(Math.toRadians(step * i.toDouble() + 45)) * sqr_2half

                    buffer.pos(posX + offx * size, posY + y + y * ringOffset, posZ + offz * size).endVertex()
                    buffer.pos(posX + offx * sizeNext, posY + 1 + y + y * ringOffset, posZ + offz * sizeNext).endVertex()
                }

                //draws a invisible triangle with an area of 0 connecting the rings
                //upper ring
                if (ringOffset != 0f)
                    buffer.pos(posX + startOffX * sizeNext, posY + 1 + y + y * ringOffset, posZ + startOffZ * sizeNext).endVertex()
            }*/

            tess.draw()
        }

        override fun postRenderType() {
        }

    }
}