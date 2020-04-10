package net.cydhra.technocracy.astronautics.content.fx

import net.cydhra.technocracy.foundation.api.fx.IParticleType
import net.cydhra.technocracy.foundation.model.fx.api.AbstractParticle
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram.ShaderUniform.UniformType.MATRIX_4x4
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram.ShaderUniform.UniformType.SAMPLER
import net.cydhra.technocracy.foundation.util.opengl.VAO
import net.cydhra.technocracy.foundation.util.opengl.VBO
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL31
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import java.nio.ByteBuffer
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.math.cos
import kotlin.math.sin


class ParticleSmoke(worldIn: World, posXIn: Double, posYIn: Double, posZIn: Double) : AbstractParticle(worldIn, posXIn, posYIn, posZIn) {

    init {
        rotation = rand.nextInt(360).toFloat()
        size = rand.nextFloat() * 4f + 1

        motionY = -0.8

        val randMovementRotation = (rand.nextInt(360) - 180)
        motionX -= sin(Math.toRadians(randMovementRotation.toDouble())) * 0.06
        motionZ += cos(Math.toRadians(randMovementRotation.toDouble())) * 0.06

        renderTime = rand.nextFloat()

        setMaxAge(20 * 5 + rand.nextInt(20 * 15)) // max 20 seconds screen time min 5 seconds
        //setMaxAge(maxAge)
        //particleAge = rand.nextInt(20 * 15)
    }

    override fun move(x: Double, y: Double, z: Double) {
        renderTime += 0.0005f * size
        rotation += 0.0005f * size

        if (onGround) {
            val rand = (rand.nextInt(360) - 180)
            motionX -= sin(Math.toRadians(rand.toDouble())) * motionY * 0.45
            motionZ += cos(Math.toRadians(rand.toDouble())) * motionY * 0.45
            motionY = -(motionY / 6f)//-ThreadLocalRandom.current().nextFloat().toDouble() / 2

            super.move(motionX, motionY, motionZ)
            return
        }

        super.move(x, y, z)
    }

    override fun renderParticle(partialTicks: Float) {
    }

    override fun getType(): IParticleType {
        return ParticleSmokeType
    }

    object ParticleSmokeType : IParticleType {

        override val perParticleRender = false
        override val name = "Smoke"
        override val maxParticles = 100000

        var vao: VAO? = null
        var vertexCount: Int = -1

        lateinit var vboData: ByteBuffer

        val vboElementSize = (1 // maxTime
                + 1 // currentTime
                + 1 // renderTime
                + 1 // rotation
                //+ 16// viewMatrix

                + 1 // size
                + 3 // pos
                )


        //val vboData = GLAllocation.createDirectFloatBuffer(MAXPARTICLES * vboElementSize)
        //val tmpBuffer = GLAllocation.createDirectFloatBuffer(4 * 4)

        lateinit var smokeShader: BasicShaderProgram
        lateinit var projectionMatrix: BasicShaderProgram.ShaderUniform
        lateinit var modelMatrix: BasicShaderProgram.ShaderUniform

        fun init() {
            smokeShader.getUniform("smoke", SAMPLER).uploadUniform(0)
            smokeShader.getUniform("noise", SAMPLER).uploadUniform(2)
            smokeShader.getUniform("lighting", SAMPLER).uploadUniform(3)
            projectionMatrix = smokeShader.getUniform("projectionMatrix", MATRIX_4x4)
            modelMatrix = smokeShader.getUniform("modelViewMatrix", MATRIX_4x4)
        }

        val MODELVIEW = Matrix4f()
        val tmpMatrix = Matrix4f()
        val position = Vector3f()

        override fun preRenderType() {

            Minecraft.getMinecraft().mcProfiler.startSection("PreRender")

            if (vao == null) {
                generateVAO()

                smokeShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shaders/smoke.vsh"), ResourceLocation("technocracy.astronautics", "shaders/smoke.fsh"), ResourceLocation("technocracy.astronautics", "shaders/smoke.gsh"), attributeBinder = Consumer {
                    GL20.glBindAttribLocation(it, 0, "position")
                    GL20.glBindAttribLocation(it, 1, "maxtime_currenttime_rendertime_rotation")
                    GL20.glBindAttribLocation(it, 2, "scale")
                    GL20.glBindAttribLocation(it, 3, "pos")

                    //GL20.glBindAttribLocation(it, 2, "modelViewMatrix")
                })

                smokeShader.start()
                init()
            } else {
                smokeShader.start()
            }

            //this.modelViewMatrix.uploadUniform(currentMatrix)
            //smokeShader.uploadUniform("modelViewMatrix", modelViewMatrix)

            MODELVIEW.load(ActiveRenderInfo.PROJECTION.asReadOnlyBuffer())
            this.projectionMatrix.uploadUniform(MODELVIEW)
            MODELVIEW.load(ActiveRenderInfo.MODELVIEW.asReadOnlyBuffer())

            this.modelMatrix.uploadUniform(MODELVIEW)


            //bind the 3 textures
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/clouds.png"))

            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2 + 1)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/lighting.png"))

            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.astronautics", "textures/fx/smoke.png"))


            vao!!.bindVAO()

            GlStateManager.disableTexture2D()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569f)
            GlStateManager.depthMask(false)

            Minecraft.getMinecraft().mcProfiler.endSection()
        }

        override fun render(particles: Stream<AbstractParticle>, partialTicks: Float): Int {
            Minecraft.getMinecraft().mcProfiler.startSection("buffer_update")

            vboData.clear()

            var i = 0
            for (p in particles) {
                vboData.putFloat(p.getMaxAge().toFloat())
                vboData.putFloat(p.getAge() + partialTicks)
                vboData.putFloat(p.renderTime)

                vboData.putFloat(p.rotation)
                vboData.putFloat(p.size)

                vboData.putFloat(p.getX(partialTicks))
                vboData.putFloat(p.getY(partialTicks))
                vboData.putFloat(p.getZ(partialTicks))

                //updateMatrix(p.interpolatePosition(position, partialTicks), p.rotation, p.size).store(vboData)
                i++
            }

            vboData.flip()

            Minecraft.getMinecraft().mcProfiler.endStartSection("uploading")
            //OpenGLObjectLoader.updateVBO(vbo_data_ID, vboData, GL15.GL_STREAM_DRAW)
            smokeShader.updateUniforms()
            Minecraft.getMinecraft().mcProfiler.endStartSection("rendering")
            GL31.glDrawArraysInstanced(GL11.GL_POINTS, 0, vertexCount, i)
            Minecraft.getMinecraft().mcProfiler.endSection()
            return i
        }

        override fun postRenderType() {
            Minecraft.getMinecraft().mcProfiler.startSection("PostRender")

            vao!!.unbindVAO()

            smokeShader.stop()

            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GlStateManager.depthMask(true)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
            Minecraft.getMinecraft().mcProfiler.endSection()
        }

        fun generateVAO() {
            vao = VAO()

            val data = floatArrayOf(
                    //-0.5f, 0.5f,
                    //-0.5f, -0.5f,
                    //0.5f, 0.5f,
                    0.5f, -0.5f
            )

            vertexCount = data.size / 2
            val vertexVBO = VBO(VBO.VBOUsage.STATIC_DRAW, data)

            val dataVBO = VBO(VBO.VBOUsage.STREAM_DRAW, maxParticles * vboElementSize * 4)
            vboData = dataVBO.mapBuffer(VBO.BufferUsage.WRITE_ONLY)

            vao!!.linkVBO(vertexVBO)
                    .addFloatAttribute(2)
            vao!!.linkVBO(dataVBO)
                    .addInstancedFloatAttribute(4, vboElementSize)//maxtime_currenttime_renderTime_rotation
                    .addInstancedFloatAttribute(1, vboElementSize)//scale
                    .addInstancedFloatAttribute(3, vboElementSize)//pos*/

            /*
            //stride is the length of data per instance (1 float = 4 bytes)
            OpenGLObjectLoader.addFloatAttributeToVAO(vao!!.vaoId, vbo_vertex_ID, 0, 2)
            OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vao!!.vaoId, vbo.vboId, 1, 4, vboElementSize, 0)//maxtime_currenttime_renderTime_rotation
            OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vao!!.vaoId, vbo.vboId, 2, 1, vboElementSize, 4)//scale
            OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vao!!.vaoId, vbo.vboId, 3, 3, vboElementSize, 5)//pos

            //OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vaoID, vbo_data_ID, 2, 4, vboElementSize, 4)//matrix coll A
            //OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vaoID, vbo_data_ID, 3, 4, vboElementSize, 8)//matrix coll B
            //OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vaoID, vbo_data_ID, 4, 4, vboElementSize, 12)//matrix coll C
            //OpenGLObjectLoader.addInstancedFloatAttributeToVAO(vaoID, vbo_data_ID, 5, 4, vboElementSize, 16)//matrix coll D*/
        }

        //currently unused cpu calculated viewmatrix
        fun updateMatrix(position: Vector3f, rotation: Float, scale: Float): Matrix4f {
            tmpMatrix.setIdentity()
            Matrix4f.translate(position, tmpMatrix, tmpMatrix)

            //remove rotation from matrix
            tmpMatrix.m00 = MODELVIEW.m00
            tmpMatrix.m01 = MODELVIEW.m10
            tmpMatrix.m02 = MODELVIEW.m20
            tmpMatrix.m10 = MODELVIEW.m01
            tmpMatrix.m11 = MODELVIEW.m11
            tmpMatrix.m12 = MODELVIEW.m21
            tmpMatrix.m20 = MODELVIEW.m02
            tmpMatrix.m21 = MODELVIEW.m12
            tmpMatrix.m22 = MODELVIEW.m22

            //apply custom rotation and scale
            rotate(rotation, tmpMatrix, tmpMatrix)
            position.set(scale, scale, scale)
            Matrix4f.scale(position, tmpMatrix, tmpMatrix)

            //upload to shader
            Matrix4f.mul(MODELVIEW, tmpMatrix, tmpMatrix)

            return tmpMatrix
        }

        fun rotate(angle: Float, src: Matrix4f, dest: Matrix4f): Matrix4f {
            val c = cos(angle)
            val s = sin(angle)

            val t00 = src.m00 * c + src.m10 * s
            val t01 = src.m01 * c + src.m11 * s
            val t02 = src.m02 * c + src.m12 * s
            val t03 = src.m03 * c + src.m13 * s
            dest.m10 = src.m00 * -s + src.m10 * c
            dest.m11 = src.m01 * -s + src.m11 * c
            dest.m12 = src.m02 * -s + src.m12 * c
            dest.m13 = src.m03 * -s + src.m13 * c
            dest.m00 = t00
            dest.m01 = t01
            dest.m02 = t02
            dest.m03 = t03
            return dest
        }
    }
}