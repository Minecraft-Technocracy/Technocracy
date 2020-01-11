package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.Minecraft
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
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import java.io.BufferedInputStream
import java.io.Closeable
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Predicate


class BasicShaderProgram(val vertexIn: ResourceLocation, val fragmentIn: ResourceLocation, val geometryIn: ResourceLocation? = null, val attributeBinder: Consumer<Int>? = null, val resourceReloader: BiConsumer<IResourceManager, Predicate<IResourceType>>? = null) : ISelectiveResourceReloadListener {
    private var programID: Int = 0
    private var vertexShaderID: Int = 0
    private var fragmentShaderID: Int = 0
    private var geometryShaderID: Int = 0
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
        if (geometryIn != null) {
            geometryShaderID = loadShader(geometryIn, GL32.GL_GEOMETRY_SHADER)
        }

        programID = OpenGlHelper.glCreateProgram()

        if (programID <= 0)
            throw IllegalStateException("Could not create shader program (returned program ID $programID)")

        OpenGlHelper.glAttachShader(programID, vertexShaderID)
        OpenGlHelper.glAttachShader(programID, fragmentShaderID)
        if (geometryShaderID != 0)
            OpenGlHelper.glAttachShader(programID, geometryShaderID)

        //No binding after linking possible without relinking it
        attributeBinder?.accept(programID)

        OpenGlHelper.glLinkProgram(programID)
        val i = OpenGlHelper.glGetProgrami(programID, OpenGlHelper.GL_LINK_STATUS)

        if (i == 0)
            throw IllegalStateException(OpenGlHelper.glGetProgramInfoLog(programID, 32768))
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
        if (geometryShaderID != 0)
            OpenGlHelper.glDeleteShader(geometryShaderID)
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
        enum class UniformType(val type: GenericType, val amount: Int) {
            SAMPLER(GenericType.SAMPLER, 1),
            INT_1(GenericType.INT, 1), INT_2(GenericType.INT, 2), INT_3(GenericType.INT, 3), INT_4(GenericType.INT, 4),
            FLOAT_1(GenericType.FLOAT, 1), FLOAT_2(GenericType.FLOAT, 2), FLOAT_3(GenericType.FLOAT, 3), FLOAT_4(GenericType.FLOAT, 4),
            MATRX_2x2(GenericType.MATRIX, 2 * 2), MATRIX_3x3(GenericType.MATRIX, 3 * 3), MATRIX_4x4(GenericType.MATRIX, 4 * 4);

            enum class GenericType {
                SAMPLER, INT, FLOAT, MATRIX
            }
        }

        private var buffer_float: FloatBuffer? = null
        private var buffer_int: IntBuffer? = null
        private var dirty = false

        init {
            when (type.type) {
                UniformType.GenericType.INT, UniformType.GenericType.SAMPLER -> buffer_int = BufferUtils.createIntBuffer(type.amount)
                UniformType.GenericType.FLOAT, UniformType.GenericType.MATRIX -> buffer_float = BufferUtils.createFloatBuffer(type.amount)
            }
        }

        fun uploadUniforms() {
            if (!shader.running) throw IllegalStateException("Shader not running")

            if (dirty) {
                when (type.type) {
                    UniformType.GenericType.INT -> uploadInt()
                    UniformType.GenericType.FLOAT -> uploadFloat()
                    UniformType.GenericType.MATRIX -> uploadMatrix()
                    UniformType.GenericType.SAMPLER -> uploadSampler()
                }
                dirty = false
            }
        }

        private fun uploadInt() {
            val tmp = buffer_int ?: return
            when (type.amount) {
                1 -> OpenGlHelper.glUniform1(uniformId, tmp)
                2 -> OpenGlHelper.glUniform2(uniformId, tmp)
                3 -> OpenGlHelper.glUniform3(uniformId, tmp)
                4 -> OpenGlHelper.glUniform4(uniformId, tmp)
            }
        }

        private fun uploadFloat() {
            val tmp = buffer_float ?: return
            when (type.amount) {
                1 -> OpenGlHelper.glUniform1(uniformId, tmp)
                2 -> OpenGlHelper.glUniform2(uniformId, tmp)
                3 -> OpenGlHelper.glUniform3(uniformId, tmp)
                4 -> OpenGlHelper.glUniform4(uniformId, tmp)
            }
        }

        private fun uploadMatrix() {
            val tmp = buffer_float ?: return
            when (type.amount) {
                2 * 2 -> OpenGlHelper.glUniformMatrix2(uniformId, false, tmp)
                3 * 3 -> OpenGlHelper.glUniformMatrix3(uniformId, false, tmp)
                4 * 4 -> GL20.glUniformMatrix4(uniformId, false, tmp)
            }
        }

        private fun uploadSampler() {
            val tmp = buffer_int ?: return
            OpenGlHelper.glUniform1i(uniformId, tmp[0])
        }

        fun uploadUniform(x: Float, y: Float, z: Float) {
            if (type.type == UniformType.GenericType.INT) {
                uploadUniform(x.toInt(), y.toInt(), z.toInt())
                return
            }
            val buffer_float = buffer_float
                    ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            if (buffer_float[0] != x || buffer_float[1] != y || buffer_float[2] != z)
                with(buffer_float) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    put(2, z)
                    dirty = true
                }
        }

        fun uploadUniform(x: Float, y: Float) {
            if (type.type == UniformType.GenericType.INT) {
                uploadUniform(x.toInt(), y.toInt())
                return
            }
            val buffer_float = buffer_float
                    ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            if (buffer_float[0] != x || buffer_float[1] != y)
                with(buffer_float) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    dirty = true
                }
        }

        fun uploadUniform(x: Float) {
            if (type.type == UniformType.GenericType.INT) {
                uploadUniform(x.toInt())
                return
            }
            val buffer_float = buffer_float
                    ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            if (buffer_float[0] != x)
                with(buffer_float) {
                    position(0)
                    put(0, x)
                    dirty = true
                }
        }

        fun uploadUniform(x: Int, y: Int, z: Int) {
            if (type.type == UniformType.GenericType.FLOAT) {
                uploadUniform(x.toFloat(), y.toFloat(), z.toFloat())
                return
            }
            val buffer_int = buffer_int ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            if (buffer_int[0] != x || buffer_int[1] != y || buffer_int[2] != z)
                with(buffer_int) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    put(2, z)
                    dirty = true
                }
        }

        fun uploadUniform(x: Int, y: Int) {
            if (type.type == UniformType.GenericType.FLOAT) {
                uploadUniform(x.toFloat(), y.toFloat())
                return
            }

            val buffer_int = buffer_int ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            if (buffer_int[0] != x || buffer_int[1] != y)
                with(buffer_int) {
                    position(0)
                    put(0, x)
                    put(1, y)
                    dirty = true
                }
        }

        fun uploadUniform(x: Int) {
            if (type.type == UniformType.GenericType.FLOAT) {
                uploadUniform(x.toFloat())
                return
            }

            val buffer_int = buffer_int ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            with(buffer_int) {
                if (this[0] != x) {
                    position(0)
                    put(0, x)
                    dirty = true
                }
            }
        }

        fun uploadUniform(x: Boolean) {
            val buffer_int = buffer_int ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            val value = if (x) 1 else 0
            with(buffer_int) {
                if (this[0] != value) {
                    position(0)
                    put(0, value)
                    dirty = true
                }
            }
        }

        fun uploadUniform(matrix4f: Matrix4f) {
            val buffer_float = buffer_float
                    ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            matrix4f.store(buffer_float)
            buffer_float.flip()
            dirty = true
        }

        fun uploadUniform(bufferIn: FloatBuffer) {
            val buffer_float = buffer_float
                    ?: run { IllegalStateException("Wrong uniform type").printStackTrace(); return }

            bufferIn.flip()
            buffer_float.position(0)
            buffer_float.put(bufferIn)
            dirty = true
        }
    }
}