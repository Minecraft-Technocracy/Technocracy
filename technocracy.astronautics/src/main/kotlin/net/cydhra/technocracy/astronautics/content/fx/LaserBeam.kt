package net.cydhra.technocracy.astronautics.content.fx

import net.cydhra.technocracy.foundation.model.fx.api.AbstractParticle
import net.cydhra.technocracy.foundation.model.fx.api.IParticleType
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.MultiTargetFBO
import net.cydhra.technocracy.foundation.util.opengl.TCOpenGlHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.*
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
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

        private val sqr_2half = sqrt(2.toDouble()) * 0.5

        var mtfbo: MultiTargetFBO? = null
        lateinit var pingPong: MultiTargetFBO

        lateinit var horizontal: BasicShaderProgram.ShaderUniform
        lateinit var expandFaktor: BasicShaderProgram.ShaderUniform
        lateinit var combines: BasicShaderProgram.ShaderUniform
        lateinit var gamma: BasicShaderProgram.ShaderUniform

        lateinit var u_xyPixelSize_zIteration: BasicShaderProgram.ShaderUniform

        var gaus: BasicShaderProgram? = null
        lateinit var kawase: BasicShaderProgram
        lateinit var blend: BasicShaderProgram
        var renderedParticle = false

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        override fun preRenderType() {
        }

        override fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {
            renderedParticle = true
            //TODO maybe instanced rendering

            //val first = particles.findFirst().get()

            val tess = Tessellator.getInstance()
            val buffer = tess.buffer

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.disableTexture2D()
            GlStateManager.depthMask(false)
            GlStateManager.disableAlpha()

            val size = max(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 * 16 / 10, 80)

            mtfbo = if (mtfbo == null) {
                MultiTargetFBO(Minecraft.getMinecraft().framebuffer, hdrFrameBuffer = false).createFramebuffer()
            } else {
                mtfbo!!.validate(Minecraft.getMinecraft().framebuffer)
            }

            val mtfbo = mtfbo!!
            mtfbo.framebufferClear()
            mtfbo.bindFramebuffer(false)

            for (first in particles) {
                val posX = first.getX(partialTicks).toDouble()
                val posY = first.getY(partialTicks).toDouble()
                val posZ = first.getZ(partialTicks).toDouble()

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

                GlStateManager.color(0.8f, 0.1f, 0.8f, 0.1f)
                //drawWobbleNoodle(posX, posY, posZ, 10, baseSize, size, first.getAge() * 8f + partialTicks, 0.2f)

                GlStateManager.color(0.4f, 0.5f, 0.4f, 0.1f)
                //drawWobbleNoodle(posX, posY, posZ, 10, baseSize - 2, size, first.getAge() * 7f + partialTicks, 0f)

                GlStateManager.color(0.3f, 0.4f, 0.3f, 0.1f)
                //drawWobbleNoodle(posX, posY, posZ, 10, baseSize - 1.3f, size, first.getAge() * 8f + partialTicks, 0f)

                GlStateManager.color(0.2f, 0.3f, 0.2f, 0.25f)
                drawWobbleNoodle(posX, posY, posZ, 10, baseSize - 0.8f, size, first.getAge() * 10f + partialTicks, 0f)

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
                tess.draw()
            }

            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val scaledWidth = scaledResolution.getScaledWidth_double() / 64
            val scaledHeight = scaledResolution.getScaledHeight_double() / 64


            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.depthMask(true)
            GlStateManager.enableCull()

            //mtfbo.framebuffer!!.bindFramebuffer(false)

            //GL44.glClearTexImage(mtfbo.textureID, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null as FloatBuffer?)

            return 0
        }

        @SubscribeEvent
        fun draw(overlay: RenderGameOverlayEvent.Pre) {
            if (!renderedParticle || overlay.type != RenderGameOverlayEvent.ElementType.ALL)
                return

            renderedParticle = false

            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val sW = scaledResolution.getScaledWidth_double()
            val sH = scaledResolution.getScaledHeight_double()

            if (gaus == null) {

                kawase = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/default.vsh"), ResourceLocation("technocracy.astronautics", "shader/kawase.fsh"))
                kawase.start()
                u_xyPixelSize_zIteration = kawase.getUniform("u_xyPixelSize_zIteration", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3)
                kawase.stop()

                blend = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/default.vsh"), ResourceLocation("technocracy.astronautics", "shader/blend.fsh"))
                blend.start()
                combines = blend.getUniform("combines", BasicShaderProgram.ShaderUniform.UniformType.INT_1)
                gamma = blend.getUniform("gamma", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
                blend.getUniform("scene", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
                blend.getUniform("bloomBlur", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(2)
                blend.stop()

                val gaus = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/default.vsh"), ResourceLocation("technocracy.astronautics", "shader/gaus.fsh"))
                gaus.start()
                horizontal = gaus.getUniform("horizontal", BasicShaderProgram.ShaderUniform.UniformType.INT_1)
                expandFaktor = gaus.getUniform("expandFaktor", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1f)
                this.gaus = gaus
                pingPong = MultiTargetFBO(sW.toInt(), sH.toInt(), false).createFramebuffer()

                kawase.start()
            } else {
                pingPong = pingPong.validate(sW.toInt(), sH.toInt())
                kawase.start()
            }

            var horizontal = true

            GlStateManager.bindTexture(mtfbo!!.textureTwo)

            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.disableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

            pingPong.framebufferClear()
            pingPong.bindFramebuffer(false)

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
            val quality = 6f
            val step = blur / quality

            val BlurBufferScale = 1

            val width = 1f / ((sW + BlurBufferScale - 1) / BlurBufferScale)
            val height = 1f / ((sH + BlurBufferScale - 1) / BlurBufferScale)

            for (i in 0..quality.toInt()) {
                u_xyPixelSize_zIteration.uploadUniform(1f / sW.toFloat(), 1f / sH.toFloat(), blur)
                blur -= step
                kawase.updateUniforms()

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
            blend.updateUniforms()

            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
            GlStateManager.bindTexture(if (!horizontal) pingPong.textureOne else pingPong.textureTwo)
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
            }
            tess.draw()
        }

        override fun postRenderType() {
        }

    }
}