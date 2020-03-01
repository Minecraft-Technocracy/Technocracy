package net.cydhra.technocracy.astronautics.client.gui

import net.cydhra.technocracy.astronautics.client.util.PlanetarySolarSystem
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.util.Interpolator
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.VAO
import net.cydhra.technocracy.foundation.util.opengl.VBO
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31
import org.lwjgl.util.glu.Project
import org.lwjgl.util.vector.Vector4f
import java.nio.FloatBuffer
import javax.vecmath.Vector3d
import kotlin.math.*
import kotlin.random.Random


class TabPlanetaryRendering(gui: TCGui) : TCTab("", gui, -1) {

    @SideOnly(Side.CLIENT)
    @Suppress("unused", "PropertyName", "SpellCheckingInspection")
    companion object {
        lateinit var vboStars: VBO
        var generatedStars = false

        var buffer: Framebuffer? = null
        var depthShader: BasicShaderProgram? = null
        lateinit var Ufarplane: BasicShaderProgram.ShaderUniform

        lateinit var basicCrtShader: BasicShaderProgram
        lateinit var Uscalar: BasicShaderProgram.ShaderUniform
        lateinit var UConvergeX: BasicShaderProgram.ShaderUniform
        lateinit var UConvergeY: BasicShaderProgram.ShaderUniform
        lateinit var UhardScan: BasicShaderProgram.ShaderUniform
        lateinit var Uwarp: BasicShaderProgram.ShaderUniform
        lateinit var UmaskDark: BasicShaderProgram.ShaderUniform
        lateinit var UmaskLight: BasicShaderProgram.ShaderUniform
        lateinit var Usaturation: BasicShaderProgram.ShaderUniform
        lateinit var UpixelScaler: BasicShaderProgram.ShaderUniform
    }

    val ic = Interpolator.InterpolationCycle<Interpolator.PosLook>()
    val gridPolater = Interpolator.InterpolationCycle<Interpolator.InterpolateFloat>()

    val planets = PlanetarySolarSystem()
    var counter = 1.0

    override fun getSizeX(): Int {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val sW = scaledResolution.scaledWidth_double

        return (sW / 1.5).toInt() + 10
    }

    override fun getSizeY(): Int {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val sH = scaledResolution.scaledHeight_double

        return (sH / 1.5).toInt() + 10
    }

    override fun update() {
        counter += 1

        planets.update()

        super.update()
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

        GlStateManager.pushMatrix()
        val mc = Minecraft.getMinecraft()

        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val sW = scaledResolution.getScaledWidth_double()
        val sH = scaledResolution.getScaledHeight_double()

        GlStateManager.color(1f, 1f, 1f, 1f)
        //mc.fontRenderer.drawString(name + " " + zoom + " " + min(5f * (zoom * 10), 5f), 8f + x, 8f + y, 4210752, false)

        val tess = Tessellator.getInstance()
        val tessBuff = tess.buffer

        if (depthShader == null) {
            basicCrtShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/default.vsh"), ResourceLocation("technocracy.astronautics", "shader/crt.fsh"))
            basicCrtShader.start()
            //UConvergeX = basicCrtShader.getUniform("ConvergeX", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3).uploadUniform(-1.0f, 0.0f, 0.5f)
            //UConvergeY = basicCrtShader.getUniform("ConvergeY", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3).uploadUniform(0.0f, -1.0f, 0.5f)
            //UhardScan = basicCrtShader.getUniform("hardScan", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(-3.0f)
            //Uwarp = basicCrtShader.getUniform("warp", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2).uploadUniform(1.0f / 16.0f, 1.0f / 16.0f)
            //UmaskDark = basicCrtShader.getUniform("maskDark", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1f)
            //UmaskLight = basicCrtShader.getUniform("maskLight", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1f)
            //Uscalar = basicCrtShader.getUniform("scalar", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2).uploadUniform(6f, 6f)
            //Usaturation = basicCrtShader.getUniform("saturation", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1.8f)
            //UpixelScaler = basicCrtShader.getUniform("pixelScaler", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(2f)
            basicCrtShader.getUniform("sampler", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
            basicCrtShader.updateUniforms()
            basicCrtShader.stop()

            depthShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/logdepth.vsh"), ResourceLocation("technocracy.astronautics", "shader/logdepth.fsh"))
            depthShader!!.start()
            Ufarplane = depthShader!!.getUniform("farplane", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
        }

        if (!generatedStars) {
            generatedStars = true
            val r = Random(207050)
            //size of farplane for now
            val xyz = 800_000f

            val perList = 2000 / 3

            val buffer = FloatBuffer.allocate(perList * 6 + ((perList * 0.5).toInt() * 3))

            generateStar(buffer, xyz, r, perList)
            generateStar(buffer, xyz, r, perList)
            generateStar(buffer, xyz, r, (perList * 0.5).toInt())
            buffer.flip()

            vboStars = VBO(VBO.VBOUsage.STATIC_DRAW, buffer.array()).addFloatAttribute(3)
        }

        buffer?.setFramebufferColor(0.12f, 0.12f, 0.12f, 0f)
        buffer = buffer.validateAndClear()

        var playerPos = ic.getInterpolated(this.zoom)

        /*val p = planets.planets[0].moons[0]

        val posX = Interpolator.linearInterpolate(p.lastposX, p.currposX, Minecraft.getMinecraft().renderPartialTicks)
        val posY = Interpolator.linearInterpolate(p.lastposY, p.currposY, Minecraft.getMinecraft().renderPartialTicks) + 256
        val posZ = Interpolator.linearInterpolate(p.lastposZ, p.currposZ, Minecraft.getMinecraft().renderPartialTicks)

        val v = Vector3d(posX, posY, posZ)
        v.add(Vector3d(playerPos.pos.x, playerPos.pos.y, playerPos.pos.z))

        playerPos = Interpolator.PosLook(v, playerPos.look)*/

        setupCameraTransform(playerPos)

        GlStateManager.enableDepth()
        GL11.glDepthMask(true)
        GlStateManager.disableAlpha()
        GlStateManager.disableTexture2D()
        GlStateManager.disableCull()
        GlStateManager.shadeModel(7425)
        GlStateManager.glLineWidth(2f)

        //apply rotation

        GL11.glRotated(playerPos.look.x, 0.0, 1.0, 0.0)
        GL11.glRotated(playerPos.look.y, 1.0, 0.0, 0.0)
        //GlStateManager.translate(-playerPos.pos.x, -playerPos.pos.y, -playerPos.pos.z)
        //GL11.glRotated(playerPos.look.z, 0.0, 0.0, 1.0)
        //GL11.glRotated(playerPos.look.y, 1.0, 0.0, 0.0)
        //GL11.glRotated(playerPos.look.x, 0.0, 1.0, 0.0)
        GL11.glRotated(playerPos.look.z, 0.0, 0.0, 1.0)

        GlStateManager.translate(-playerPos.pos.x, -playerPos.pos.y, -playerPos.pos.z)
        val perList = 2000 / 3
        val max = perList * 2 + (perList * 0.5).toInt()

        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glPointSize(0.2f)
        vboStars.bindVBO()
        //GL31.glDrawArraysInstanced(GL11.GL_POINTS, 0, max, perList)
        GL11.glPointSize(0.5f)
        GL11.glDrawArrays(GL11.GL_POINTS, perList, perList)
        //GL31.glDrawArraysInstanced(GL11.GL_POINTS, perList, max, perList)
        GL11.glPointSize(4f)
        GL11.glDrawArrays(GL11.GL_POINTS, perList * 2, (perList * 0.5).toInt())
        //GL31.glDrawArraysInstanced(GL11.GL_POINTS, perList * 2, max, (perList * 0.5).toInt())

        vboStars.unbindVBO()
        GlStateManager.translate(playerPos.pos.x, playerPos.pos.y, playerPos.pos.z)
        depthShader!!.start()


        //remove rotation
        /*GL11.glRotated(-playerPos.look.x, 0.0, 1.0, 0.0)
        GL11.glRotated(-playerPos.look.y, 1.0, 0.0, 0.0)
        GL11.glRotated(-playerPos.look.z, 0.0, 0.0, 1.0)

        //translate and then rotate
        GL11.glRotated(playerPos.look.z, 0.0, 0.0, 1.0)
        GL11.glRotated(playerPos.look.y, 1.0, 0.0, 0.0)
        GL11.glRotated(playerPos.look.x, 0.0, 1.0, 0.0)*/

        val sizeEarth = 256

        planets.render(playerPos)

        /*//earth
        //offset position 1 radius down so 0 0 0 is on the top plane of the planet
        drawCube(sizeEarth, 0f, 0.467f, 0.745f, Vector3f(0f, -sizeEarth.toFloat(), 0f), playerPos.pos)

        //moon
        val rot = MathHelper.wrapDegrees(counter + Minecraft.getMinecraft().renderPartialTicks.toDouble()).toFloat()
        val rad = Math.toRadians(rot.toDouble()).toFloat()
        GlStateManager.rotate(-90f, 0f, 1f, 0f)
        GlStateManager.rotate(-35f, 0f, 0f, 1f)

        drawCube(sizeEarth / 4, 162 / 255f, 168 / 255f, 174 / 255f, Vector3f(2000f * sin(rad), -sizeEarth.toFloat(), 3000f * -cos(rad)), playerPos.pos, Vector3f(-rot, -0f, 35f))
        GlStateManager.rotate(35f, 0f, 0f, 1f)
        GlStateManager.rotate(90f, 0f, 1f, 0f)

        GlStateManager.rotate(46f, 0f, 1f, 0f)
        //mars
        drawCube(sizeEarth / 2, 193 / 255f, 68 / 255f, 14 / 255f, Vector3f(8000f, -sizeEarth.toFloat(), 0f), playerPos.pos)

        GlStateManager.rotate(-46f, 0f, 1f, 0f)

        //sun
        drawCube(sizeEarth * 100, 1f, 1f, 1f, Vector3f(400000f, -sizeEarth.toFloat(), 0f), playerPos.pos)

        GlStateManager.rotate(-76f, 0f, 1f, 0f)
        //saturn
        drawCube(sizeEarth * 10, 234 / 255f, 214 / 255f, 184 / 255f, Vector3f(30000f, -sizeEarth.toFloat(), 0f), playerPos.pos, hasRings = true)

        GlStateManager.rotate(76f, 0f, 1f, 0f)*/

        depthShader!!.stop()

        mc.entityRenderer.setupOverlayRendering()

        basicCrtShader.start()
        //Uscalar.uploadUniform(12f, 12f)
        //UmaskDark.uploadUniform(0.5f)
        //UmaskLight.uploadUniform(1f)
        //UhardScan.uploadUniform(-2f)
        //Usaturation.uploadUniform(4f)
        //UpixelScaler.uploadUniform(1.5f)
        //UConvergeX.uploadUniform(-1f,0f,1f)
        //UConvergeY.uploadUniform(0f,-1f,1f)
        //basicCrtShader.updateUniforms()

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.disableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)

        //crtShader!!.render(partialTicks)
        buffer?.bindFramebufferTexture()
        mc.framebuffer.bindFramebuffer(true)
        //crtBuffer?.bindFramebuffer(false)

        GlStateManager.translate(sW / 1.5 / 4.0 + 1, sH / 1.5 / 4.0 + 1, 0.0)
        GlStateManager.enableTexture2D()
        tessBuff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        tessBuff.pos(0.0, 0.0, 1.0).tex(0.0, 1.0).endVertex()
        tessBuff.pos(0.0, sH / 1.5, 1.0).tex(0.0, 0.0).endVertex()
        tessBuff.pos(sW / 1.5, sH / 1.5, 1.0).tex(1.0, 0.0).endVertex()
        tessBuff.pos(sW / 1.5, 0.0, 0.0).tex(1.0, 1.0).endVertex()
        tess.draw()

        basicCrtShader.stop()


        GlStateManager.popMatrix()
    }


    fun generateStar(buffer: FloatBuffer, distance: Float, r: Random, amount: Int) {
        for (i in 0 until amount) {
            val yaw = 2 * Math.PI * r.nextDouble()
            val pitch = acos(2.0 * r.nextDouble() - 1.0) - Math.PI / 2.0
            buffer.put((cos(pitch) * cos(yaw) * distance).toFloat())
            buffer.put((cos(pitch) * sin(yaw) * distance).toFloat())
            buffer.put((sin(pitch) * distance).toFloat())
        }
    }


    fun setupCameraTransform(playerPos: Interpolator.PosLook) {
        val mc = Minecraft.getMinecraft()
        GlStateManager.matrixMode(5889)
        GlStateManager.loadIdentity()

        val farPlaneDistance = 500_000f
        Project.gluPerspective(110f, mc.displayWidth.toFloat() / mc.displayHeight.toFloat(), 0.005f, 200_000f)
        GlStateManager.matrixMode(5888)
        GlStateManager.loadIdentity()


        Ufarplane.uploadUniform(farPlaneDistance)
        depthShader!!.updateUniforms()
    }

    var zoom = 0f

    override fun handleMouseInput() {
        val i = Mouse.getEventDWheel()
        if (i > 0)
            zoom += 0.1f
        else if (i < 0)
            zoom -= 0.1f
    }

    override fun init() {
        val earthSize = 256
        val distanceScaler = 0.2

        //499
        val earth = PlanetarySolarSystem.Moon(earthSize, Vector4f(0f, 0.467f, 0.745f, 1f), false, Vector3d(0.1, 0.0, 0.0), distance = 300000.0 * distanceScaler)
        val earthMoon = PlanetarySolarSystem.Moon(earthSize / 4, Vector4f(162 / 255f, 168 / 255f, 174 / 255f, 1f), false, Vector3d(1.0, 0.0, 0.0), distance = 3000.0)
        earthMoon.setInitOrbitRotation(Vector3d(0.0, 0.0, 35.0))
        earthMoon.faceToPlanet = true

        val mars = PlanetarySolarSystem.Moon(earthSize / 2, Vector4f(193 / 255f, 68 / 255f, 14 / 255f, 1f), false, Vector3d(0.5, 0.0, 0.0), distance = 460000.0 * distanceScaler)
        mars.setInitOrbitRotation(Vector3d(-20.0, 0.0, 0.0))

        val venus = PlanetarySolarSystem.Moon(earthSize, Vector4f(193 / 255f, 143 / 255f, 23 / 255f, 1f), false, Vector3d(0.3, 0.0, 0.0), distance = 220_000.0 * distanceScaler)
        venus.setInitOrbitRotation(Vector3d(-80.0, 0.0, 0.0))

        val mercury = PlanetarySolarSystem.Moon((earthSize * 0.4).roundToInt(), Vector4f(162 / 255f, 168 / 255f, 174 / 255f, 1f), false, Vector3d(0.3, 0.0, 0.0), distance = 120_000.0 * distanceScaler)
        mercury.setInitOrbitRotation(Vector3d(80.0, 0.0, 0.0))

        val jupiter = PlanetarySolarSystem.Moon(earthSize * 10, Vector4f(216 / 255f, 202 / 255f, 157 / 255f, 1f), false, Vector3d(0.55, 0.0, 0.0), distance = 700_000.0 * distanceScaler)
        jupiter.setInitOrbitRotation(Vector3d(-20.0, 0.0, 0.0))

        val saturn = PlanetarySolarSystem.Moon(earthSize * 9, Vector4f(234 / 255f, 214 / 255f, 184 / 255f, 1f), true, Vector3d(0.35, 0.0, 0.0), distance = 900_000.0 * distanceScaler)
        saturn.setInitOrbitRotation(Vector3d(-180.0, 0.0, 0.0))

        val sun = PlanetarySolarSystem.Planet(earthSize * 30, Vector4f(1f, 1f, 1f, 1f), false, 0.0, 0.0, 0.0)
        earth.moons.add(earthMoon)
        sun.moons.add(earth)
        sun.moons.add(mars)
        sun.moons.add(venus)
        sun.moons.add(mercury)
        sun.moons.add(jupiter)
        sun.moons.add(saturn)

        planets.planets.clear()
        planets.addPlanet(sun)


        ic.clear()
        //add offset to prevent clipping

        //374

        ic.addStep(PlanetarySolarSystem.PlanetLock(earthMoon, Vector3d(0.0, 1.62 + 2, 0.0), Vector3d(0.0, 90.0, 0.0)), 0.0f)
        ic.addStep(PlanetarySolarSystem.PlanetLock(earthMoon, Vector3d(0.0, 162.0 + 2, 0.0), Vector3d(0.0, 90.0, 0.0)), 10.0f)
        ic.addStep(PlanetarySolarSystem.PlanetLock(earth, Vector3d(0.0, 1262.0, 0.0), Vector3d(0.0, 90.0, 0.0)), 30.0f)
        ic.addStep(PlanetarySolarSystem.PlanetLock(earth, Vector3d(0.0, 5262.0, 0.0), Vector3d(0.0, 90.0, 0.0)), 40.0f)
        ic.addStep(Interpolator.PosLook(Vector3d(0.0, 100000.0, 00.0), Vector3d(0.0, 90.0, 0.0)), 120.0f)
        /*ic.addStep(Interpolator.PosLook(Vector3d(0.0, 1.62 + 0.25, 0.0), Vector3d(0.0, 5.0, 0.0)), 0.0f)
        ic.addStep(Interpolator.PosLook(Vector3d(0.0, 16.2, 0.0), Vector3d(0.0, 5.0, 0.0)), 10.0f)
        ic.addStep(Interpolator.PosLook(Vector3d(0.0, 126.2, 100.0), Vector3d(0.0, 90.0, 0.0)), 20.0f)
        ic.addStep(Interpolator.PosLook(Vector3d(0.0, 100000.0, 00.0), Vector3d(0.0, 90.0, 0.0)), 60.0f)*/

        //ic.addStep(Interpolator.PosLook(Vector3f(100.0, 0.0, 8000.0), Vector3f(-15f, 0.0, 0.0)), 40.0)
        //ic.addStep(Interpolator.PosLook(Vector3d(00.0, 0.0, 8000.0), Vector3d(180.0, 0.0, 0.0)), 40.0f)
        //ic.addStep(Interpolator.PosLook(Vector3d(0.0, 0.0, 8000.0), Vector3d(360.0, 0.0, 0.0)), 60.0f)
        //ic.addStep(Interpolator.PosLook(Vector3d(7800.0, 1.62 + 0.25 - 128, 0.0), Vector3d(360.0, 5.0, 0.0)), 70.0f)

        gridPolater.clear()
        gridPolater.addStep(Interpolator.InterpolateFloat(0f), 0f)
        gridPolater.addStep(Interpolator.InterpolateFloat(256f), 5f)
        gridPolater.addStep(Interpolator.InterpolateFloat(128f), 10f)
        gridPolater.addStep(Interpolator.InterpolateFloat(64f), 30f)
        gridPolater.addStep(Interpolator.InterpolateFloat(32f), 75f)
        gridPolater.addStep(Interpolator.InterpolateFloat(16f), 100f)
        gridPolater.addStep(Interpolator.InterpolateFloat(8f), 200f)
        gridPolater.addStep(Interpolator.InterpolateFloat(4f), 600f)
        gridPolater.addStep(Interpolator.InterpolateFloat(2f), 2000f)
    }

}