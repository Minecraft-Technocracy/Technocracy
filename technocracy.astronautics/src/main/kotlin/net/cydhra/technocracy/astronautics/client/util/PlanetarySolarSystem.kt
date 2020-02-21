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
import org.lwjgl.util.vector.Vector4f
import javax.vecmath.Vector3d
import kotlin.math.*


class PlanetarySolarSystem() {

    val planets = mutableListOf<Planet>()
    private val gridPolater = Interpolator.InterpolationCycle<Interpolator.InterpolateFloat>()
    var debug: Boolean = false

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

    fun render(posPlayer: Interpolator.PosLook) {
        for (p in planets)
            p.render(posPlayer)
    }

    fun update() {
        for (p in planets)
            p.update(0.0, 0.0, 0.0)
    }

    class Planet(size: Int, color: Vector4f, hasRings: Boolean = false, posX: Double, posY: Double, posZ: Double, rotationSpeed: Vector3d = Vector3d()) : Moon(size, color, hasRings, Vector3d(), rotationSpeed, 0.0) {

        override val glow = true

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

        fun render(posPlayer: Interpolator.PosLook) {

            system.drawCube(this, posPlayer)

            for (m in this.moons)
                m.render(posPlayer, posX, posY, posZ)
        }
    }


    open class Moon(val size: Int, val color: Vector4f, val hasRings: Boolean = false, val orbitSpeed: Vector3d = Vector3d(), val rotationSpeed: Vector3d = Vector3d(), val distance: Double) {

        lateinit var system: PlanetarySolarSystem

        val moons = mutableListOf<Moon>()

        open val glow = false

        val rotations = Vector3d()
        val lastRotations = Vector3d()
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

            //calculate new position
            var roll = 0.0//Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.z))
            val yaw = Math.toRadians(MathHelper.wrapDegrees(-rotationsOrbit.x))
            val pitch = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.y + 90))

            currposX = (xPosParent + (-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)) * distance / 0.2)
            currposY = (yPosParent + (-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)) * distance / 0.2)
            currposZ = (zPosParent + cos(yaw) * sin(pitch) * distance / 0.2)

            lastRotations.set(lastRotations)

            if (faceToPlanet) {
                val diffX = xPosParent - currposX
                val diffY = yPosParent - currposY
                val diffZ = zPosParent - currposZ

                val dist = sqrt(diffX * diffX + diffZ * diffZ)

                val yaw = (atan2(diffZ, diffX) * 180.0 / Math.PI)
                val pitch = (-(atan2(diffY, dist) * 180.0 / Math.PI))

                //todo roll?
                rotations.set(90 - yaw, pitch + 90, 0.0)
            } else {
                rotations.add(rotationSpeed)
            }

            roll = Math.toRadians(MathHelper.wrapDegrees(rotationsOrbit.z))

            currposX = (xPosParent + (-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)) * distance / 0.2)
            currposY = (yPosParent + (-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)) * distance / 0.2)
            currposZ = (zPosParent + cos(yaw) * sin(pitch) * distance / 0.2)

            for (m in moons)
                m.update(currposX, currposY, currposZ)
        }

        fun render(posPlayer: Interpolator.PosLook, xPosParent: Double, yPosParent: Double, zPosParent: Double) {
            posX = Interpolator.linearInterpolate(lastposX, currposX, Minecraft.getMinecraft().renderPartialTicks)
            posY = Interpolator.linearInterpolate(lastposY, currposY, Minecraft.getMinecraft().renderPartialTicks)
            posZ = Interpolator.linearInterpolate(lastposZ, currposZ, Minecraft.getMinecraft().renderPartialTicks)

            if (system.debug) {
                //draw line from center to parent
                GlStateManager.translate(-posPlayer.pos.x, -posPlayer.pos.y, -posPlayer.pos.z)
                GL11.glColor4f(1f, 0f, 0f, 1f)
                GL11.glBegin(GL11.GL_LINES)
                GL11.glVertex3d(xPosParent, yPosParent, zPosParent)
                GL11.glVertex3d(posX, posY, posZ)
                GL11.glEnd()
                GlStateManager.translate(posPlayer.pos.x, posPlayer.pos.y, posPlayer.pos.z)
            }

            system.drawCube(this, posPlayer)

            for (m in moons)
                m.render(posPlayer, posX, posY, posZ)
        }
    }

    fun drawTopBottom(factor: Float, size: Float, colorOrig: Vector4f) {
        val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
        val sizei = size.toInt()
        var height = size

        val tes = Tessellator.getInstance()
        val buf = tes.buffer
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

        //make to face red
        var color = if (debug) Vector4f(1f, 0f, 0f, 1f) else colorOrig

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
            color = colorOrig
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

    fun drawCube(planet: Moon, player: Interpolator.PosLook) {

        val size = planet.size
        val color = planet.color
        val posCube = Vector3d(planet.posX, planet.posY, planet.posZ)
        val tilt = planet.rotations

        val posPlayer = player.pos
        GlStateManager.translate(posCube.x - posPlayer.x, posCube.y - posPlayer.y, posCube.z - posPlayer.z)

        if (debug) {
            //draw calculated looking vector of planet
            val roll = 0.0//Math.toRadians(MathHelper.wrapDegrees(tilt.z))
            val yaw = Math.toRadians(MathHelper.wrapDegrees(-tilt.x))
            val pitch = Math.toRadians(MathHelper.wrapDegrees(tilt.y))

            val scaleX = ((-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)))
            val scaleY = ((-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)))
            val scaleZ = (cos(yaw) * sin(pitch))

            GlStateManager.color(0f, 1f, 0f, 1f)
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(((size * 3) * scaleX).toFloat(), ((size * 3) * scaleY).toFloat(), ((size * 3) * scaleZ).toFloat())
            GL11.glEnd()
        }

        //GL11.glRotated(tilt.x, 0.0, 1.0, 0.0)
        //GL11.glRotated(tilt.y, 1.0, 0.0, 0.0)
        //GL11.glRotated(tilt.z, 0.0, 0.0, 1.0)

        //GL11.glRotated(tilt.z, 0.0, 0.0, 1.0)
        //GL11.glRotated(tilt.y, 1.0, 0.0, 0.0)
        //GL11.glRotated(tilt.x, 0.0, 1.0, 0.0)

        GL11.glRotated(tilt.x, 0.0, 1.0, 0.0)
        GL11.glRotated(tilt.y, 1.0, 0.0, 0.0)
        GL11.glRotated(tilt.z, 0.0, 0.0, 1.0)

        //use rough distance for performance and quick checking
        val distance = sqrt((posPlayer.x - posCube.x).pow(2.0) + (posPlayer.y - posCube.y).pow(2.0) + (posPlayer.z - posCube.z).pow(2.0)) - size - 1.6

        val currentGrid = gridPolater.getCurrent(distance.toFloat())
        val nextGrid = gridPolater.getNext(distance.toFloat())
        val currentScale = currentGrid.value.value

        val sizef = size.toFloat()
        GlStateManager.enableBlend()

        //render smaller inner cube used for depth clipping
        GlStateManager.colorMask(true, true, true, true)
        if (!planet.glow) {
            GlStateManager.color(0.4f * color.x, 0.4f * color.y, 0.4f * color.z, 1.0f)
        } else {
            GlStateManager.color(color.x, color.y, color.z, 1.0f)
        }

        val offset = 0.1
        var bb = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        bb = bb.grow(sizef.toDouble())
        bb = bb.shrink(offset)

        OpenGLBoundingBox.drawBoundingBox(bb)

        if (planet.glow) {
            GlStateManager.depthMask(false)
            GlStateManager.color(color.x, color.y, color.z, 0.04f)
            bb = bb.grow(sizef / 3.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            bb = bb.grow(sizef / 16.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            GlStateManager.color(color.x, color.y, color.z, 0.03f)
            bb = bb.grow(sizef / 3.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            bb = bb.grow(sizef / 14.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            GlStateManager.color(color.x, color.y, color.z, 0.02f)
            bb = bb.grow(sizef / 2.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            bb = bb.grow(sizef / 15.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            GlStateManager.color(color.x, color.y, color.z, 0.01f)
            bb = bb.grow(sizef / 2.0)
            OpenGLBoundingBox.drawBoundingBox(bb)
            GlStateManager.depthMask(true)
        }

        GlStateManager.colorMask(true, true, true, true)


        val alpha = if (nextGrid == null) 1f else 1f - Interpolator.linearInterpolate(0.0f, 1f, currentGrid.time, nextGrid.time, distance.toFloat())
        color.setW(alpha)

        drawTopBottom(currentScale, sizef, color)
        drawSide(currentScale, sizef, color)
        drawFace(currentScale, sizef, color)


        //GL11.glRotated(-tilt.z, 0.0, 0.0, 1.0)
        //GL11.glRotated(-tilt.y, 1.0, 0.0, 0.0)
        //GL11.glRotated(-tilt.x, 0.0, 1.0, 0.0)


        //GL11.glRotated(-tilt.x, 0.0, 1.0, 0.0)
        //GL11.glRotated(-tilt.y, 1.0, 0.0, 0.0)
        //GL11.glRotated(-tilt.z, 0.0, 0.0, 1.0)

        //draw line in all 8 directions to make the rotation more visible
        if (debug) {
            GlStateManager.color(0f, 0f, 1f, 1f)
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(sizef * 2, 0f, 0f)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(-sizef * 2, 0f, 0f)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(0f, sizef * 2, 0f)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(0f, -sizef * 2, 0f)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(0f, 0f, sizef * 2)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glVertex3f(0f, 0f, -sizef * 2)
            GL11.glEnd()
        }

        GL11.glRotated(-tilt.z, 0.0, 0.0, 1.0)
        GL11.glRotated(-tilt.y, 1.0, 0.0, 0.0)
        GL11.glRotated(-tilt.x, 0.0, 1.0, 0.0)


        if (planet.hasRings) {
            GlStateManager.color(color.x, color.y, color.z, 1.0f)

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

        GlStateManager.translate(-(posCube.x - posPlayer.x), -(posCube.y - posPlayer.y), -(posCube.z - posPlayer.z))
    }

    class PlanetLock(val lock: Moon, val offset: Vector3d = Vector3d(), look: Vector3d) : Interpolator.PosLook(Vector3d(), look) {
        override val pos: Vector3d
            get() {
                val rot = Vector3d(lock.rotations)

                if (lock.faceToPlanet) {
                    //rotate by 90° so it is the upper face instead of the one facing the planet
                    rot.y = rot.y - 90
                }

                val roll = Math.toRadians(MathHelper.wrapDegrees(rot.z))
                val yaw = Math.toRadians(MathHelper.wrapDegrees(-rot.x))
                val pitch = Math.toRadians(MathHelper.wrapDegrees(rot.y))

                val scaleX = ((-cos(roll) * sin(yaw) * sin(pitch) - sin(roll) * cos(pitch)))
                val scaleY = ((-sin(roll) * sin(yaw) * sin(pitch) + cos(roll) * cos(pitch)))
                val scaleZ = (cos(yaw) * sin(pitch))

                val posX = Interpolator.linearInterpolate(lock.lastposX, lock.currposX, Minecraft.getMinecraft().renderPartialTicks)
                val posY = Interpolator.linearInterpolate(lock.lastposY, lock.currposY, Minecraft.getMinecraft().renderPartialTicks)
                val posZ = Interpolator.linearInterpolate(lock.lastposZ, lock.currposZ, Minecraft.getMinecraft().renderPartialTicks)
                val p = Vector3d(posX, posY, posZ)
                //todo calc other offsets
                p.add(Vector3d((lock.size + offset.y) * scaleX, (lock.size + offset.y) * scaleY, (lock.size + offset.y) * scaleZ))
                return p
            }

        override val look: Vector3d
            get() {
                val rot = Vector3d(lock.rotations)

                if (lock.faceToPlanet) {
                    rot.setX(180 - lock.rotations.x)
                    rot.setY(180 + lock.rotations.y)
                }
                rot.add(super.look)

                rot.set(MathHelper.wrapDegrees(rot.x), MathHelper.wrapDegrees(rot.y), MathHelper.wrapDegrees(rot.z))

                return rot

            }
    }
}