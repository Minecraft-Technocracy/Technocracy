package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.client.util.JsonException
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.BufferUtils
import java.io.BufferedInputStream
import java.io.Closeable
import java.lang.IllegalStateException
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Predicate


class BasicShaderProgram(val vertexIn: ResourceLocation, val fragmentIn: ResourceLocation, val attributeBinder: Consumer<Int>? = null, val resourceReloader: BiConsumer<IResourceManager, Predicate<IResourceType>>? = null) : ISelectiveResourceReloadListener {
    companion object {
        private val matrixBuffer_4 = BufferUtils.createFloatBuffer(4 * 4)
        private val matrixBuffer_3 = BufferUtils.createFloatBuffer(3 * 3)
    }

    private var programID: Int = 0
    private var vertexShaderID: Int = 0
    private var fragmentShaderID: Int = 0
    private var running = false
    private val uniform = mutableListOf<ShaderUniform>()

    init {
        (Minecraft.getMinecraft().resourceManager as SimpleReloadableResourceManager).registerReloadListener(this)
        loadShader()
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate<IResourceType>) {
        if (resourcePredicate.test(VanillaResourceType.SHADERS)) {
            cleanUp()
            loadShader()
        }

        resourceReloader?.accept(resourceManager, resourcePredicate)
    }

    private fun loadShader() {
        vertexShaderID = loadShader(vertexIn, OpenGlHelper.GL_VERTEX_SHADER)
        fragmentShaderID = loadShader(fragmentIn, OpenGlHelper.GL_FRAGMENT_SHADER)

        programID = OpenGlHelper.glCreateProgram()

        if (programID <= 0)
            throw IllegalStateException("Could not create shader program (returned program ID $programID)")

        OpenGlHelper.glAttachShader(programID, vertexShaderID)
        OpenGlHelper.glAttachShader(programID, fragmentShaderID)

        attributeBinder?.accept(programID)

        OpenGlHelper.glLinkProgram(programID)
        val i = OpenGlHelper.glGetProgrami(programID, OpenGlHelper.GL_LINK_STATUS)

        if (i == 0)
            throw IllegalStateException(OpenGlHelper.glGetProgramInfoLog(programID, 32768))
    }

    fun applyAndRender() {
        start()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        GL11.glBegin(4)
        GL11.glTexCoord2d(0.0, 1.0)
        GL11.glVertex2d(0.0, 0.0)
        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex2d(0.0, scaledResolution.scaledHeight_double)
        GL11.glTexCoord2d(1.0, 0.0)
        GL11.glVertex2d(scaledResolution.scaledWidth_double, scaledResolution.scaledHeight_double)
        GL11.glTexCoord2d(1.0, 0.0)
        GL11.glVertex2d(scaledResolution.scaledWidth_double, scaledResolution.scaledHeight_double)
        GL11.glTexCoord2d(1.0, 1.0)
        GL11.glVertex2d(scaledResolution.scaledWidth_double, 0.0)
        GL11.glTexCoord2d(0.0, 1.0)
        GL11.glVertex2d(0.0, 0.0)
        GL11.glEnd()
        stop()
    }

    fun start() {
        running = true
        OpenGlHelper.glUseProgram(programID)
    }

    fun stop() {
        OpenGlHelper.glUseProgram(0)
        running = false
    }

    fun getUniform(variableName: String, type: ShaderUniform.UniformType): ShaderUniform {
        val tmp = ShaderUniform(type, OpenGlHelper.glGetUniformLocation(this.programID, variableName), variableName, this)
        uniform.add(tmp)
        return tmp
    }

    fun updateUniforms() {
        for (u in uniform)
            u.uploadUniforms()
    }

    fun cleanUp() {
        OpenGlHelper.glDeleteShader(vertexShaderID)
        OpenGlHelper.glDeleteShader(fragmentShaderID)
        OpenGlHelper.glDeleteShader(programID)
    }

    private fun loadShader(shader: ResourceLocation, type: Int): Int {
        val resource = Minecraft.getMinecraft().resourceManager.getResource(shader)

        try {
            val bytes = IOUtils.toByteArray(BufferedInputStream(resource.inputStream))
            val buffer = BufferUtils.createByteBuffer(bytes.size)
            buffer.put(bytes)
            buffer.position(0)
            val i = OpenGlHelper.glCreateShader(type)
            OpenGlHelper.glShaderSource(i, buffer)
            OpenGlHelper.glCompileShader(i)

            if (OpenGlHelper.glGetShaderi(i, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
                val s = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(i, 32768))
                val jsonexception = JsonException("Couldn't compile " + resource.resourcePackName + " program: " + s)
                jsonexception.setFilenameAndFlush(shader.resourcePath)
                throw jsonexception
            }
            return i
        } finally {
            IOUtils.closeQuietly(resource as Closeable)
        }
    }

    class ShaderUniform(val type: UniformType, var uniformId: Int, var uniformName: String, var shader: BasicShaderProgram) {
        enum class UniformType(val type: Int, val amount: Int) {
            SAMPLER(3, 0), INT_1(0, 1), INT_2(0, 2), INT_3(0, 3), INT_4(0, 4), FLOAT_1(1, 1), FLOAT_2(1, 2), FLOAT_3(1, 3), FLOAT_4(1, 4), MATRX_2x2(2, 2 * 2), MATRIX_3x3(2, 3 * 3), MATRIX_4x4(2, 4 * 4)
        }

        private var buffer_float: FloatBuffer? = null
        private var buffer_int: IntBuffer? = null
        private var dirty = false

        init {
            when (type.type) {
                0 -> buffer_int = BufferUtils.createIntBuffer(type.amount)
                1, 2 -> buffer_float = BufferUtils.createFloatBuffer(type.amount)
                3 -> buffer_int = BufferUtils.createIntBuffer(1)
            }
        }

        fun uploadUniforms() {
            if (!shader.running) throw IllegalStateException("Shader not running")

            if (dirty) {
                when (type.type) {
                    0 -> uploadInt()
                    1 -> uploadFloat()
                    2 -> uploadMatrix()
                    3 -> uploadSampler()
                }
                dirty = false
            }
        }

        private fun uploadInt() {
            when (type.amount) {
                1 -> OpenGlHelper.glUniform1(uniformId, buffer_int!!)
                2 -> OpenGlHelper.glUniform2(uniformId, buffer_int!!)
                3 -> OpenGlHelper.glUniform3(uniformId, buffer_int!!)
                4 -> OpenGlHelper.glUniform4(uniformId, buffer_int!!)
            }
        }

        private fun uploadFloat() {
            when (type.amount) {
                1 -> OpenGlHelper.glUniform1(uniformId, buffer_float!!)
                2 -> OpenGlHelper.glUniform2(uniformId, buffer_float!!)
                3 -> OpenGlHelper.glUniform3(uniformId, buffer_float!!)
                4 -> OpenGlHelper.glUniform4(uniformId, buffer_float!!)
            }
        }

        private fun uploadMatrix() {
            when (type.amount) {
                2 * 2 -> OpenGlHelper.glUniformMatrix2(uniformId, false, buffer_float!!)
                3 * 3 -> OpenGlHelper.glUniformMatrix3(uniformId, false, buffer_float!!)
                4 * 4 -> GL20.glUniformMatrix4(uniformId, false, buffer_float!!)
            }
        }

        private fun uploadSampler() {
            OpenGlHelper.glUniform1i(uniformId, buffer_int!![0])
        }

        fun uploadUniform(x: Float, y: Float, z: Float) {
            if (buffer_float!![0] != x || buffer_float!![1] != y || buffer_float!![2] != z)
                with(buffer_float!!) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    put(2, z)
                    dirty = true
                }
        }

        fun uploadUniform(x: Float, y: Float) {
            if (buffer_float!![0] != x || buffer_float!![1] != y)
                with(buffer_float!!) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    dirty = true
                }
        }

        fun uploadUniform(x: Float) {
            if (buffer_float!![0] != x)
                with(buffer_float!!) {
                    position(0)
                    put(0, x)
                    dirty = true
                }
        }

        fun uploadUniform(x: Int, y: Int, z: Int) {
            if (buffer_int!![0] != x || buffer_int!![1] != y || buffer_int!![2] != z)
                with(buffer_int!!) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    put(2, z)
                    dirty = true
                }
        }

        fun uploadUniform(x: Int, y: Int) {
            if (buffer_int!![0] != x || buffer_int!![1] != y)
                with(buffer_int!!) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    dirty = true
                }
        }

        fun uploadUniform(x: Int) {
            if (buffer_int!![0] != x)
                with(buffer_int!!) {
                    position(0)
                    put(0, x)
                    dirty = true
                }
        }

        fun uploadUniform(matrix4f: Matrix4f) {
            matrix4f.store(buffer_float)
            buffer_float!!.flip()
            dirty = true
        }

        fun uploadUniform(matrix4f: FloatBuffer) {
            buffer_float!!.position(0)
            buffer_float!!.put(matrix4f)
            dirty = true
        }

        /*var uniformCache = HashMap<Int, Any>()

        fun uploadUniform(name: String, x: Float, y: Float, z: Float) {
            val id = getUniformLocation(name)
            var obj: FloatArray? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as FloatArray
            }

            if (obj == null || obj[0] != x || obj[1] != y || obj[2] != z) {
                OpenGlHelper.glUniform3()
                ARBShaderObjects.glUniform3fARB(id, x, y, z)
                uniformCache[id] = floatArrayOf(x, y, z)
            }
        }

        fun uploadUniform(name: String, x: Float, y: Float) {
            val id = getUniformLocation(name)
            var obj: FloatArray? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as FloatArray
            }

            if (obj == null || obj[0] != x || obj[1] != y) {
                ARBShaderObjects.glUniform2fARB(id, x, y)
                uniformCache[id] = floatArrayOf(x, y)
            }
        }

        fun uploadUniform(name: String, x: Float) {
            val id = getUniformLocation(name)
            var obj: Float? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as Float
            }

            if (obj == null || obj != x) {
                ARBShaderObjects.glUniform1fARB(id, x)
                uniformCache[id] = x
            }
        }

        fun uploadUniform(name: String, x: Int, y: Int, z: Int) {
            val id = getUniformLocation(name)
            var obj: IntArray? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as IntArray
            }

            if (obj == null || obj[0] != x || obj[1] != y || obj[2] != z) {
                ARBShaderObjects.glUniform3iARB(id, x, y, z)
                uniformCache[id] = intArrayOf(x, y, z)
            }
        }

        fun uploadUniform(name: String, x: Int, y: Int) {
            val id = getUniformLocation(name)
            var obj: IntArray? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as IntArray
            }

            if (obj == null || obj[0] != x || obj[1] != y) {
                ARBShaderObjects.glUniform2iARB(id, x, y)
                uniformCache[id] = intArrayOf(x, y)
            }
        }

        fun uploadUniform(name: String, x: Int) {
            val id = getUniformLocation(name)
            var obj: Int? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as Int
            }

            if (obj == null || obj != x) {
                OpenGlHelper.glUniform1i(id, x)
                uniformCache[id] = x
            }
        }

        fun uploadUniform(name: String, matrix4f: Matrix4f) {
            val id = getUniformLocation(name)
            var obj: Matrix4f? = null

            if (uniformCache.containsKey(id)) {
                obj = uniformCache[id] as Matrix4f
            }

            if (obj == null || obj != matrix4f) {

                matrix4f.store(matrixBuffer_4)
                matrixBuffer_4.flip()

                ARBShaderObjects.glUniformMatrix4ARB(id, false, matrixBuffer_4)
                uniformCache[id] = matrix4f
            }
        }*/
    }
}