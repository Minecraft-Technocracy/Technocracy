package net.cydhra.technocracy.foundation.util.opengl

import net.cydhra.technocracy.foundation.util.CompoundBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.client.util.JsonException
import net.minecraft.command.CommandReload
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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

@SideOnly(Side.CLIENT)
class BasicShaderProgram(val vertexIn: ResourceLocation, val fragmentIn: ResourceLocation, val geometryIn: ResourceLocation? = null, val attributeBinder: Consumer<Int>? = null, val resourceReloader: BiConsumer<IResourceManager, Predicate<IResourceType>>? = null, val init: (BasicShaderProgram.() -> Unit)? = null) : ISelectiveResourceReloadListener {

    private var programID: Int = 0
    private var vertexShaderID: Int = 0
    private var fragmentShaderID: Int = 0
    private var geometryShaderID: Int = 0
    private var running = false
    private val uniform = mutableListOf<ShaderUniform>()

    init {
        (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener(this)
        init?.let { it() }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate<IResourceType>) {
        if (resourcePredicate.test(VanillaResourceType.SHADERS)) {
            reloadShader(resourceManager)
        }

        resourceReloader?.accept(resourceManager, resourcePredicate)
    }

    fun reloadShader(resourceManager: IResourceManager) {
        cleanUp()
        loadShader(resourceManager)
        addUniforms()
    }

    private fun addUniforms() {
        for (u in uniform) {
            u.uniformId = OpenGlHelper.glGetUniformLocation(this.programID, u.uniformName)
            u.markDirty()
        }
        updateUniforms()
    }

    private fun loadShader(resourceManager: IResourceManager) {
        vertexShaderID = loadShader(resourceManager, vertexIn, OpenGlHelper.GL_VERTEX_SHADER)
        fragmentShaderID = loadShader(resourceManager, fragmentIn, OpenGlHelper.GL_FRAGMENT_SHADER)
        if (geometryIn != null) {
            geometryShaderID = loadShader(resourceManager, geometryIn, GL32.GL_GEOMETRY_SHADER)
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

        GL20.glDetachShader(programID, vertexShaderID)
        GL20.glDetachShader(programID, fragmentShaderID)
        OpenGlHelper.glDeleteShader(vertexShaderID)
        OpenGlHelper.glDeleteShader(fragmentShaderID)

        if (geometryShaderID != 0)
            OpenGlHelper.glDeleteShader(geometryShaderID)

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
        OpenGlHelper.glDeleteProgram(programID)
    }

    private fun loadShader(resourceManager: IResourceManager, shader: ResourceLocation, type: Int): Int {
        val resource = resourceManager.getResource(shader)

        try {
            val bytes = IOUtils.toByteArray(BufferedInputStream(resource.inputStream))
            val buffer = BufferUtils.createByteBuffer(bytes.size)
            buffer.put(bytes)
            buffer.position(0)
            val string = String(bytes)
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

    @SideOnly(Side.CLIENT)
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

        internal fun markDirty() {
            dirty = true
        }

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
                notify(notifyShaderNotRunning); return
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

        fun uploadUniform(vararg ints: Int): ShaderUniform {
            if (type.type == UniformType.GenericType.FLOAT) {
                uploadUniform(*ints.asSequence().map { it.toFloat() }.toList().toFloatArray())
                return this
            }

            val buffer = buffer_int
                    ?: run { notify(notifyWrongType); return this }
            if (type.amount != ints.size) {
                notify(notifyWrongSize); return this
            }

            ints.asSequence().forEachIndexed { i, value ->
                if (buffer[i] != value) {
                    buffer.put(i, value)
                    dirty = true
                }
            }
            return this
        }

        fun uploadUniform(vararg doubles: Double): ShaderUniform {
            uploadUniform(*doubles.asSequence().map { it.toFloat() }.toList().toFloatArray())
            return this
        }

        fun uploadUniform(vararg floats: Float): ShaderUniform {
            if (type.type == UniformType.GenericType.INT) {
                uploadUniform(*floats.asSequence().map { it.toInt() }.toList().toIntArray())
                return this
            }

            val buffer = buffer_float
                    ?: run { notify(notifyWrongType); return this }
            if (type.amount != floats.size) {
                notify(notifyWrongSize); return this
            }

            floats.asSequence().forEachIndexed { i, value ->
                if (buffer[i] != value) {
                    buffer.put(i, value)
                    dirty = true
                }
            }
            return this
        }

        fun uploadUniform(x: Boolean): ShaderUniform {
            val value = if (x) 1 else 0
            uploadUniform(value)
            return this
        }

        fun uploadUniform(matrix4f: Matrix4f): ShaderUniform {
            val buffer = buffer_float
                    ?: run { notify(notifyWrongType); return this }
            if (type.amount != 4 * 4) {
                notifyWrongSize; return this
            }

            matrix4f.store(buffer)
            buffer.flip()
            dirty = true
            return this
        }

        fun uploadUniform(bufferIn: FloatBuffer): ShaderUniform {
            val buffer = buffer_float
                    ?: run { notify(notifyWrongType); return this }

            bufferIn.flip()
            buffer.position(0)
            buffer.put(bufferIn)
            dirty = true
            return this
        }
    }
}