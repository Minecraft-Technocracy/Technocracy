package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import java.util.HashMap
import org.lwjgl.BufferUtils
import java.io.StringWriter
import java.util.function.BiConsumer
import java.util.function.Predicate


class BasicShaderProgram(val vertexIn: ResourceLocation, val fragmentIn: ResourceLocation, val resourceReloader: BiConsumer<IResourceManager, Predicate<IResourceType>>? = null) : ISelectiveResourceReloadListener {
    companion object {
        private val matrixBuffer = BufferUtils.createFloatBuffer(16)
    }

    init {
        (Minecraft.getMinecraft().resourceManager as SimpleReloadableResourceManager).registerReloadListener(this)
        loadShader()
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate<IResourceType>) {
        if (resourcePredicate.test(VanillaResourceType.SHADERS)) {
            cleanUp()
            loadShader()
        }

        if (resourceReloader != null)
            resourceReloader.accept(resourceManager, resourcePredicate)
    }

    fun loadShader() {
        var buffer = StringWriter()
        IOUtils.copy(Minecraft.getMinecraft().resourceManager.getResource(vertexIn).inputStream, buffer, "UTF-8")
        val vertex = buffer.toString()
        buffer = StringWriter()
        IOUtils.copy(Minecraft.getMinecraft().resourceManager.getResource(fragmentIn).inputStream, buffer, "UTF-8")
        val fragment = buffer.toString()

        try {
            vertexShaderID = loadShader(vertex, GL20.GL_VERTEX_SHADER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            fragmentShaderID = loadShader(fragment, GL20.GL_FRAGMENT_SHADER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        programID = GL20.glCreateProgram()
        GL20.glAttachShader(programID, vertexShaderID)
        GL20.glAttachShader(programID, fragmentShaderID)

        GL20.glBindAttribLocation(programID, 0, "position")

        GL20.glLinkProgram(programID)
        GL20.glValidateProgram(programID)
    }

    var programID: Int = 0
    var vertexShaderID: Int = 0
    var fragmentShaderID: Int = 0

    var uniforms = HashMap<String, Int>()

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
        GL20.glUseProgram(programID)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun getUniformLocation(variableName: String): Int {
        if (uniforms.containsKey(variableName)) {
            return uniforms[variableName]!!
        }
        val i = ARBShaderObjects.glGetUniformLocationARB(this.programID, variableName)
        uniforms[variableName] = i
        return i
    }

    fun cleanUp() {
        GL20.glDetachShader(programID, vertexShaderID)
        GL20.glDetachShader(programID, fragmentShaderID)
        GL20.glDeleteShader(vertexShaderID)
        GL20.glDeleteShader(fragmentShaderID)
        GL20.glDeleteProgram(programID)
    }

    private fun loadShader(Shader: String, type: Int): Int {
        var shader = 0
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(type)
            if (shader == 0) {
                return 0
            }
            ARBShaderObjects.glShaderSourceARB(shader, Shader as CharSequence)
            ARBShaderObjects.glCompileShaderARB(shader)
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))
            }
            return shader
        } catch (exc: Exception) {
            ARBShaderObjects.glDeleteObjectARB(shader)
            throw exc
        }

    }

    fun getLogInfo(obj: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, 35716))
    }

    var uniformCache = HashMap<Int, Any>()

    fun uploadUniform(name: String, x: Float, y: Float, z: Float) {
        val id = getUniformLocation(name)
        var obj: FloatArray? = null

        if (uniformCache.containsKey(id)) {
            obj = uniformCache[id] as FloatArray
        }

        if (obj == null || obj[0] != x || obj[1] != y || obj[2] != z) {
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
            ARBShaderObjects.glUniform1iARB(id, x)
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

            matrix4f.store(matrixBuffer)
            matrixBuffer.flip()

            ARBShaderObjects.glUniformMatrix4ARB(id, false, matrixBuffer)
            uniformCache[id] = matrix4f
        }
    }
}