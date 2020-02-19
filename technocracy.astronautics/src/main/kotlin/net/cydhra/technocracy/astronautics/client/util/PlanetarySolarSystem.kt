package net.cydhra.technocracy.astronautics.client.util

import net.cydhra.technocracy.foundation.util.Interpolator
import net.cydhra.technocracy.foundation.util.color
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.cydhra.technocracy.foundation.util.pos
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import javax.vecmath.Vector3d
import kotlin.math.*


class PlanetarySolarSystem() {

    val planets = mutableListOf<Planet>()
    private val gridPolater = Interpolator.InterpolationCycle<Interpolator.InterpolateFloat>()

    init {
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

    fun addPlanet(p: Planet) {
        planets.add(p.setPlanetarySystem(this))
    }

    fun render(posPlayer: Vector3d) {
        for (p in planets)
            p.render(posPlayer)
    }

    fun update() {
        for (p in planets)
            p.update(0.0, 0.0, 0.0)
    }

    class Planet(size: Int, color: Vector4f, hasRings: Boolean = false, posX: Double, posY: Double, posZ: Double, rotationSpeed: Vector3d = Vector3d()) : Moon(size, color, hasRings, Vector3d(), rotationSpeed, 0.0) {
        init {
            this.posX = posX
            this.posY = posY
            this.posZ = posZ
        }

        override fun setPlanetarySystem(system: PlanetarySolarSystem): Planet {
            this.system = system
            for (m in moons)
                m.setPlanetarySystem(system)
            return this
        }

        fun render(posPlayer: Vector3d) {

            system.drawCube(size, color.x, color.y, color.z, Vector3d(posX, posY, posZ), posPlayer, Vector3d(), hasRings)

            for (m in this.moons)
                m.render(posPlayer, posX, posY, posZ)
        }
    }


    open class Moon(val size: Int, val color: Vector4f, val hasRings: Boolean = false, val orbitSpeed: Vector3d = Vector3d(), val rotationSpeed: Vector3d = Vector3d(), val distance: Double) {

        lateinit var system: PlanetarySolarSystem

        val moons = mutableListOf<Moon>()

        val rotations = Vector3d()
        val rotationsOrbit = Vector3d()

        var posX = 0.0
        var posY = 0.0
        var posZ = 0.0

        var lastposX = 0.0
        var lastposY = 0.0
        var lastposZ = 0.0
        var currposX = 0.0
        var currposY = 0.0
        var currposZ = 0.0

        var faceToPlanet: Boolean = false

        open fun setPlanetarySystem(system: PlanetarySolarSystem): Moon {
            this.system = system
            for (m in moons)
                m.setPlanetarySystem(system)
            return this
        }

        fun setInitOrbitRotation(rotations: Vector3d): Moon {
            rotationsOrbit.set(rotations)
            return this
        }

        fun setInitRotation(rotations: Vector3d): Moon {
            this.rotations.set(rotations)
            return this
        }

        fun update(xPosParent: Double, yPosParent: Double, zPosParent: Double) {

            lastposX = currposX
            lastposY = currposY
            lastposZ = currposZ

            rotationsOrbit.add(orbitSpeed)
            rotations.add(rotationSpeed)
            val roll = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.z))
            val yaw = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.x))
            val pitch = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.y + 90))

            currposX = (xPosParent + (-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)) * distance)
            currposY = (yPosParent + (-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)) * distance)
            currposZ = (zPosParent + cos(yaw) * sin(pitch) * distance)

            for (m in moons)
                m.update(currposX, currposY, currposZ)
        }

        fun render(posPlayer: Vector3d, xPosParent: Double, yPosParent: Double, zPosParent: Double) {
            val roll = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.z))
            val yaw = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.x))
            val pitch = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.y + 90))

            //val tmpX = (xPosParent + (-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)) * distance)
            //val tmpY = (yPosParent + (-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)) * distance)
            //val tmpZ = (zPosParent + cos(yaw) * sin(pitch) * distance)

            posX = Interpolator.linearInterpolate(lastposX, currposX, Minecraft.getMinecraft().renderPartialTicks)
            posY = Interpolator.linearInterpolate(lastposY, currposY, Minecraft.getMinecraft().renderPartialTicks)
            posZ = Interpolator.linearInterpolate(lastposZ, currposZ, Minecraft.getMinecraft().renderPartialTicks)

            val rot = if (faceToPlanet) {
                val v = Vector3d(rotationsOrbit)
                v.negate()
                v.z = rotationsOrbit.z
                v.y = v.y - 90
                v.add(rotations)
                v
            } else rotations

            system.drawCube(size, color.x, color.y, color.z, Vector3d(posX, posY, posZ), posPlayer, rot, hasRings)

            for (m in moons)
                m.render(posPlayer, posX, posY, posZ)
        }
    }

    fun drawTopBottom(factor: Float, size: Float, color: Vector4f) {
        val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
        val sizei = size.toInt()
        var height = size

        val tes = Tessellator.getInstance()
        val buf = tes.buffer
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        for (unused in 0..1) {
            var b = true
            for (i in -sizei..sizei) {
                if (i != -sizei && i != sizei && i % test != 0) continue

                buf.pos(i.toFloat(), height, -size).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(i.toFloat(), height, size).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(-size, height, i.toFloat()).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(size, height, i.toFloat()).color(color, if (!b) color.w else 1f).endVertex()

                b = !b
            }
            height *= -1
        }
        tes.draw()
    }

    fun drawSide(factor: Float, size: Float, color: Vector4f) {
        val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
        val sizei = size.toInt()
        var height = size

        val tes = Tessellator.getInstance()
        val buf = tes.buffer
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        for (unused in 0..1) {
            var b = true
            for (i in -sizei..sizei) {
                if (i != -sizei && i != sizei && i % test != 0) continue
                buf.pos(height, i.toFloat(), -size).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(height, i.toFloat(), size).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(height, -size, i.toFloat()).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(height, size, i.toFloat()).color(color, if (!b) color.w else 1f).endVertex()
                b = !b
            }
            height *= -1
        }
        tes.draw()
    }

    fun drawFace(factor: Float, size: Float, color: Vector4f) {
        val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
        val sizei = size.toInt()
        var height = size

        val tes = Tessellator.getInstance()
        val buf = tes.buffer
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        for (unused in 0..1) {
            var b = true
            for (i in -sizei..sizei) {
                if (i != -sizei && i != sizei && i % test != 0) continue
                buf.pos(i.toFloat(), -size, height).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(i.toFloat(), size, height).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(-size, i.toFloat(), height).color(color, if (!b) color.w else 1f).endVertex()
                buf.pos(size, i.toFloat(), height).color(color, if (!b) color.w else 1f).endVertex()
                b = !b
            }
            height *= -1
        }
        tes.draw()
    }

    fun drawCube(size: Int, r: Float, g: Float, b: Float, posCube: Vector3d, posPlayer: Vector3d, tilt: Vector3d = Vector3d(0.0, 0.0, 0.0), hasRings: Boolean = false) {

        GlStateManager.translate(posCube.x, posCube.y, posCube.z)
        GL11.glRotated(tilt.z, 0.0, 0.0, 1.0)
        GL11.glRotated(tilt.x, 0.0, 1.0, 0.0)
        GL11.glRotated(tilt.y, 1.0, 0.0, 0.0)

        //use rough distance for performance and quick checking
        val distance = sqrt((posPlayer.x - posCube.x).pow(2.0) + (posPlayer.y - posCube.y).pow(2.0) + (posPlayer.z - posCube.z).pow(2.0)) - size - 1.6

        val currentGrid = gridPolater.getCurrent(distance.toFloat())
        val nextGrid = gridPolater.getNext(distance.toFloat())
        val currentScale = currentGrid.value.value

        val sizef = size.toFloat()
        GlStateManager.enableBlend()

        //render smaller inner cube used for depth clipping
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.color(0.4f * r, 0.4f * g, 0.4f * b, 1.0f)

        val offset = 0.1
        var bb = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        bb = bb.grow(sizef.toDouble())
        bb = bb.shrink(offset)

        OpenGLBoundingBox.drawBoundingBox(bb)

        GlStateManager.colorMask(true, true, true, true)


        val alpha = if (nextGrid == null) 1f else 1f - Interpolator.linearInterpolate(0.0f, 1f, currentGrid.time, nextGrid.time, distance.toFloat())
        val color = Vector4f(r, g, b, alpha)

        drawTopBottom(currentScale, sizef, color)
        drawSide(currentScale, sizef, color)
        drawFace(currentScale, sizef, color)

        GL11.glRotated(-tilt.y, 1.0, 0.0, 0.0)
        GL11.glRotated(-tilt.x, 0.0, 1.0, 0.0)
        GL11.glRotated(-tilt.z, 0.0, 0.0, 1.0)


        GlStateManager.color(r, g, b, 1.0f)

        if (hasRings) {
            //rings
            var d = sizef
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3f(-sizef - d, 0f, -sizef - d)
            GL11.glVertex3f(-sizef - d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, -sizef - d)
            GL11.glEnd()
            d = sizef * 1.25f
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3f(-sizef - d, 0f, -sizef - d)
            GL11.glVertex3f(-sizef - d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, -sizef - d)
            GL11.glEnd()
            d = sizef * 1.33f
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3f(-sizef - d, 0f, -sizef - d)
            GL11.glVertex3f(-sizef - d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, -sizef - d)
            GL11.glEnd()
            d = sizef * 0.8f
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3f(-sizef - d, 0f, -sizef - d)
            GL11.glVertex3f(-sizef - d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, -sizef - d)
            GL11.glEnd()
            d = sizef * 1.4f
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3f(-sizef - d, 0f, -sizef - d)
            GL11.glVertex3f(-sizef - d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, -sizef - d)
            GL11.glEnd()
            d = sizef * 1.5f
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glVertex3f(-sizef - d, 0f, -sizef - d)
            GL11.glVertex3f(-sizef - d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, sizef + d)
            GL11.glVertex3f(sizef + d, 0f, -sizef - d)
            GL11.glEnd()
        }

        GlStateManager.translate(-posCube.x, -posCube.y, -posCube.z)
    }

    class PlanetLock(val lock: Moon, val offset: Vector3d = Vector3d(), look: Vector3d) : Interpolator.PosLook(Vector3d(), look) {
        override val pos: Vector3d
            get() {

                val roll = Math.toRadians(MathHelper.wrapDegrees(lock.rotationsOrbit.z))
                val yaw = Math.toRadians(MathHelper.wrapDegrees(lock.rotationsOrbit.x))
                val pitch = Math.toRadians(MathHelper.wrapDegrees(lock.rotationsOrbit.y + 90))

                val scaleX = ((-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)))
                val scaleY = ((-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)))
                val scaleZ = (cos(yaw) * sin(pitch))


                val posX = Interpolator.linearInterpolate(lock.lastposX, lock.currposX, Minecraft.getMinecraft().renderPartialTicks)
                val posY = Interpolator.linearInterpolate(lock.lastposY, lock.currposY, Minecraft.getMinecraft().renderPartialTicks)
                val posZ = Interpolator.linearInterpolate(lock.lastposZ, lock.currposZ, Minecraft.getMinecraft().renderPartialTicks)
                val p = Vector3d(posX, posY, posZ)
                p.add(Vector3d(offset.x , offset.y + 100, offset.z ))
                p.sub(Vector3d(lock.size * scaleX, lock.size * scaleY, lock.size * scaleZ ))
                return p
            }

        override val look: Vector3d
            get() = Vector3d(MathHelper.wrapDegrees(lock.rotations.x), MathHelper.wrapDegrees(80.0), -MathHelper.wrapDegrees(lock.rotations.z))
    }
}