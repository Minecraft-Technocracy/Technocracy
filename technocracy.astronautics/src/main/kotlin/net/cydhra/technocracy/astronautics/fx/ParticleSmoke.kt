package net.cydhra.technocracy.astronautics.fx

import net.cydhra.technocracy.foundation.fx.AbstractParticle
import net.cydhra.technocracy.foundation.fx.IParticleType
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.BufferUtils
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.sin


class ParticleSmoke(worldIn: World, posXIn: Double, posYIn: Double, posZIn: Double) : AbstractParticle(worldIn, posXIn, posYIn, posZIn) {

    var time = 0f

    init {
        rotation = ThreadLocalRandom.current().nextInt(360).toFloat()
        size = ThreadLocalRandom.current().nextFloat() * 4f + 1

        motionY = -0.8

        val rand = (ThreadLocalRandom.current().nextInt(360) - 180)
        motionX -= sin(Math.toRadians(rand.toDouble())) * 0.06
        motionZ += cos(Math.toRadians(rand.toDouble())) * 0.06

        time = ThreadLocalRandom.current().nextFloat()

        setMaxAge(20 * 5 + ThreadLocalRandom.current().nextInt(300))
    }

    override fun move(x: Double, y: Double, z: Double) {
        time += 0.0005f * size

        if (onGround) {

            val rand = (ThreadLocalRandom.current().nextInt(360) - 180)
            motionX -= sin(Math.toRadians(rand.toDouble())) * motionY * 0.35
            motionZ += cos(Math.toRadians(rand.toDouble())) * motionY * 0.35
            motionY = ThreadLocalRandom.current().nextFloat().toDouble()  / 2

            super.move(motionX, motionY, motionZ)
            return
        }

        super.move(x, y, z)
    }

    override fun renderParticle() {
        ParticleSmokeType.updateMatrix(Vector3f((posX - Minecraft.getMinecraft().renderManager.viewerPosX).toFloat(), (posY - Minecraft.getMinecraft().renderManager.viewerPosY).toFloat(), (posZ - Minecraft.getMinecraft().renderManager.viewerPosZ).toFloat()), 0f, size, ParticleSmokeType.currentMatrix)

        //upload time to shader
        ParticleSmokeType.smokeShader.uploadUniform("time", time)

        //TODO instanced rendering

        //render vao
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, ParticleSmokeType.vertexCount)
    }

    override fun getType(): IParticleType {
        return ParticleSmokeType
    }

    object ParticleSmokeType : IParticleType {

        val currentMatrix: Matrix4f = Matrix4f()

        var vao: Int = -1
        var vertexCount: Int = -1
        lateinit var smokeShader: BasicShaderProgram

        override fun preRenderType() {

            GL11.glPushMatrix()

            currentMatrix.load(ActiveRenderInfo.MODELVIEW.asReadOnlyBuffer())

            if (vao == -1) {
                vao = generateVAO()

                smokeShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/smoke.vsh"), ResourceLocation("technocracy.astronautics", "shader/smoke.fsh"), attributeBinder = Consumer { GL20.glBindAttribLocation(it, 0, "position") })

                smokeShader.start()
                smokeShader.uploadUniform("sampler0", 0)
                smokeShader.uploadUniform("sampler1", 2)
            } else {
                smokeShader.start()
            }

            //bind the 2 textures
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/clouds.png"))

            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/smoke.png"))

            //bind vao
            GL30.glBindVertexArray(vao)
            GL20.glEnableVertexAttribArray(0)

            GlStateManager.disableTexture2D()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.disableLighting()
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569f)
            GlStateManager.depthMask(false)
        }

        override fun postRenderType() {
            //unbind vao
            GL30.glBindVertexArray(0)

            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GlStateManager.depthMask(true)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
            smokeShader.stop()

            GL11.glPopMatrix()
        }

        fun updateMatrix(position: Vector3f, rotation: Float, scale: Float, viewMatrix: Matrix4f) {
            val modelMatrix = Matrix4f()
            Matrix4f.translate(position, modelMatrix, modelMatrix)

            //remove roation from matrix
            modelMatrix.m00 = viewMatrix.m00
            modelMatrix.m01 = viewMatrix.m10
            modelMatrix.m02 = viewMatrix.m20
            modelMatrix.m10 = viewMatrix.m01
            modelMatrix.m11 = viewMatrix.m11
            modelMatrix.m12 = viewMatrix.m21
            modelMatrix.m20 = viewMatrix.m02
            modelMatrix.m21 = viewMatrix.m12
            modelMatrix.m22 = viewMatrix.m22

            //apply custom rotation and scale
            Matrix4f.rotate(Math.toRadians(rotation.toDouble()).toFloat(), Vector3f(0f, 0f, 1f), modelMatrix, modelMatrix)
            Matrix4f.scale(Vector3f(scale, scale, scale), modelMatrix, modelMatrix)

            //upload to shader
            val modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null)
            smokeShader.uploadUniform("modelViewMatrix", modelViewMatrix)

            val projection = Matrix4f().load(ActiveRenderInfo.PROJECTION.asReadOnlyBuffer()) as Matrix4f
            smokeShader.uploadUniform("projectionMatrix", projection)
        }

        fun generateVAO(): Int {
            //generate vao
            val vaoID = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(vaoID)

            //generate vbo
            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)

            val data = floatArrayOf(
                    -0.5f, 0.5f,
                    -0.5f, -0.5f,
                    0.5f, 0.5f,
                    0.5f, -0.5f)

            //put data into buffer
            val buffer = BufferUtils.createFloatBuffer(data.size)
            buffer.put(data)
            buffer.flip()

            //put buffer into vbo
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)

            val attributeNumber = 0
            val coordinateSize = 2

            GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0)

            vertexCount = data.size / coordinateSize

            //unbind vbo and vao
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
            GL30.glBindVertexArray(0)

            return vaoID
        }
    }
}