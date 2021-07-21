package net.cydhra.technocracy.astronautics.content.fx

import net.cydhra.technocracy.foundation.api.fx.AbstractParticle
import net.cydhra.technocracy.foundation.api.fx.IParticleType
import net.cydhra.technocracy.foundation.client.shader.RefractionEffect
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.util.vector.Vector2f
import java.util.stream.Stream


class RefrectParticle(worldIn: World, posXIn: Double, posYIn: Double, posZIn: Double) : AbstractParticle(worldIn, posXIn, posYIn, posZIn) {
    init {
        particleMaxAge = 10
    }

    override fun getType(): IParticleType {
        return ParticleRefractType
    }

    override fun renderParticle(partialTicks: Float) {
    }

    object ParticleRefractType : IParticleType {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun update(event: TickEvent.ClientTickEvent) {
            time.x++
            time.y++
            time.y += 0.5f
        }


        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        override val name = "refract"
        override val perParticleRender = false
        override val maxParticles = -1
        override val mutex = null

        //var built = false

        //lateinit var refracShader: BasicShaderProgram
        //lateinit var damaged: BasicShaderProgram.ShaderUniform
        //lateinit var timeUniform: BasicShaderProgram.ShaderUniform

        var time = Vector2f(0f, 0f)

        //lateinit var buffer: Framebuffer

        var render = 0
        //var rebuild = true

        override fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {

            RefractionEffect.time.uploadUniform(time.x, time.y)
            RefractionEffect.beginRendering()

            //refracShader.updateUniforms()

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.disableTexture2D()
            GlStateManager.depthMask(true)
            GlStateManager.disableAlpha()
            GlStateManager.disableCull()


            var render = 0
            for (particle in particles) {

                RefractionEffect.updateBackBuffer()

                /*OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().framebuffer.framebufferObject)
                OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.framebufferObject)

                GL30.glBlitFramebuffer(0, 0, buffer.framebufferWidth, buffer.framebufferHeight, 0, 0, buffer.framebufferWidth, buffer.framebufferHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST)

                OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Minecraft.getMinecraft().framebuffer.framebufferObject)
                OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buffer.framebufferObject)*/

                render++

                val tess = Tessellator.getInstance()
                val buffer = tess.buffer

                val posX = particle.getX(partialTicks)
                val posY = particle.getY(partialTicks).toDouble()
                val posZ = particle.getZ(partialTicks)

                //damaged.uploadUniform(posX.toDouble(), posY, posZ.toDouble())
                //refracShader.updateUniforms()

                val step = 360 / 4.0

                OpenGLBoundingBox.drawTexturedBoundingBox(AxisAlignedBB(0.0, 0.0, 0.0, 5.0, 5.0, 5.0).offset(posX.toDouble(), posY, posZ.toDouble()))

                /*
                buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_NORMAL)

                val rotationOffset = 10.0
                val rotationOffsetRadians = Math.toRadians(rotationOffset)
                    for (i in 0..1) {
                        val currStep = step * i.toDouble() + 45  +  rotationOffset
                        val rad = Math.toRadians(currStep)

                        val sqr_half = sqrt(2.toDouble()) * 0.5

                        val offx = sin(rad) * sqr_half
                        val offz = -cos(rad) * sqr_half

                        val offxNext = sin(rad + rotationOffsetRadians) * sqr_half
                        val offzNext = -cos(rad + rotationOffsetRadians) * sqr_half

                        val vec = Vec3d(posX + offx, posY, posZ + offz)
                        val normal = vec.normalize()

                        val vec2 = Vec3d(posX + offxNext, posY + 1, posZ + offzNext)
                        val normal2 = vec.normalize()

                        buffer.pos(vec.x, vec.y, vec.z).tex(0.0,0.0).normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat()).endVertex()
                        buffer.pos(vec2.x, vec2.y, vec2.z).tex(1.0,0.0).normal(normal2.x.toFloat(), normal2.y.toFloat(), normal2.z.toFloat()).endVertex()

                        buffer.pos(vec.x +1, vec.y, vec.z).tex(0.0,0.0).normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat()).endVertex()
                        buffer.pos(vec2.x +1, vec2.y, vec2.z).tex(1.0,0.0).normal(normal2.x.toFloat(), normal2.y.toFloat(), normal2.z.toFloat()).endVertex()
                }
                tess.draw()*/
            }

            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.depthMask(true)
            GlStateManager.enableCull()
            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

            return render
        }

        /*@SubscribeEvent
        fun draw(overlay: RenderGameOverlayEvent.Pre) {
            if (true || overlay.type != RenderGameOverlayEvent.ElementType.ALL)
                return

            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val sW = scaledResolution.getScaledWidth_double()
            val sH = scaledResolution.getScaledHeight_double()

            GlStateManager.enableTexture2D()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.disableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)


            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

            //GlStateManager.bindTexture(this.buffer.framebufferTexture)

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


            GlStateManager.bindTexture(0)

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

            GlStateManager.enableAlpha()
        }*/

        override fun preRenderType() {
            RefractionEffect.preRenderType(ResourceLocation("technocracy.foundation", "textures/fx/hex_diffuse.png"),ResourceLocation("technocracy.foundation", "textures/fx/hex_normal.png"))
            RefractionEffect.colorShift.uploadUniform(false)


            /*
            if (!built) {
                built = true

                buffer = Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false)

                refracShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shaders/refrag.vsh"), ResourceLocation("technocracy.astronautics", "shaders/refrag.fsh"))
                damaged = refracShader.getUniform("damaged", BasicShaderProgram.ShaderUniform.UniformType.INT_1)
                timeUniform = refracShader.getUniform("time", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)
                refracShader.getUniform("image", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
                refracShader.getUniform("image_normal", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(2)
                refracShader.getUniform("image_diffuse", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(3)
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
                if (rebuild) {
                    rebuild = false
                    refracShader.reloadShader()
                }
            } else {
                rebuild = true
            }

            val mc = Minecraft.getMinecraft()

            refracShader.start()

            timeUniform.uploadUniform(time.x + mc.renderPartialTicks, time.y + mc.renderPartialTicks)

            buffer = buffer.validateAndClear()
            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)

            GlStateManager.bindTexture(buffer.framebufferTexture)
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/hex_normal.png"))
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2 + 1)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/hex_diffuse.png"))
*/
        }

        override fun postRenderType() {
            RefractionEffect.postRenderType()
            /*
            refracShader.stop()

            GlStateManager.bindTexture(0)
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            GlStateManager.bindTexture(0)

            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)*/
        }
    }
}