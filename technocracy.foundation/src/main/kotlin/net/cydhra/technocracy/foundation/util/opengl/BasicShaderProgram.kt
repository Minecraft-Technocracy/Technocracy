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
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32
import org.lwjgl.util.vector.Matrix4f
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
                val ex = JsonException("Couldn't compile " + resource.resourcePackName + " program: " + s)
                ex.setFilenameAndFlush(shader.resourcePath)
                throw ex
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

        private var notified = false
        private val notifyWrongSize = "The uniform has the wrong size"
        private val notifyWrongType = "Wrong uniform type"
        private val notifyShaderNotRunning = "The shader is not running"

        private fun notify(msg: String) {
            if (!notified) {
                IllegalStateException(msg).printStackTrace()
                notified = true
            }
        }

        init {
            when (type.type) {
                UniformType.GenericType.INT, UniformType.GenericType.SAMPLER -> buffer_int = BufferUtils.createIntBuffer(type.amount)
                UniformType.GenericType.FLOAT, UniformType.GenericType.MATRIX -> buffer_float = BufferUtils.createFloatBuffer(type.amount)
            }
        }

        fun uploadUniforms() {
            if (!shader.running) {
                notifyShaderNotRunning; return
            }

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

        fun uploadUniform(vararg ints: Int) {
            if (type.type == UniformType.GenericType.FLOAT) {
                uploadUniform(*ints.asSequence().map { it.toFloat() }.toList().toFloatArray())
                return
            }

            val buffer = buffer_int
                    ?: run { notify(notifyWrongType); return }
            if (type.amount != ints.size) {
                notify(notifyWrongSize); return
            }

            ints.asSequence().forEachIndexed { i, value ->
                if (buffer[i] != value) {
                    buffer.put(i, value)
                    dirty = true
                }
            }
        }

        fun uploadUniform(vararg floats: Float) {
            if (type.type == UniformType.GenericType.INT) {
                uploadUniform(*floats.asSequence().map { it.toInt() }.toList().toIntArray())
                return
            }

            val buffer = buffer_float
                    ?: run { notify(notifyWrongType); return }
            if (type.amount != floats.size) {
                notify(notifyWrongSize); return
            }

            floats.asSequence().forEachIndexed { i, value ->
                if (buffer[i] != value) {
                    buffer.put(i, value)
                    dirty = true
                }
            }
        }

        fun uploadUniform(x: Boolean) {
            val value = if (x) 1 else 0
            uploadUniform(value)
        }

        fun uploadUniform(matrix4f: Matrix4f) {
            val buffer = buffer_float
                    ?: run { notify(notifyWrongType); return }
            if (type.amount != 4 * 4) {
                notifyWrongSize; return
            }

            matrix4f.store(buffer)
            buffer.flip()
            dirty = true
        }

        fun uploadUniform(bufferIn: FloatBuffer) {
            val buffer = buffer_float
                    ?: run { notify(notifyWrongType); return }

            bufferIn.flip()
            buffer.position(0)
            buffer.put(bufferIn)
            dirty = true
        }
    }
}