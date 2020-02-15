package net.cydhra.technocracy.astronautics.content.tileentity

import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.OwnerShipTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.cydhra.technocracy.foundation.util.Interpolator
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import kotlin.math.*


class TileEntityRocketController : AggregatableTileEntity(), TEInventoryProvider, TCTileEntityGuiProvider, DynamicInventoryCapability.CustomItemStackStackLimit {

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getStackLimit(slot: Int, stack: ItemStack, default: Int): Int {
        if (currentRocket != null) {
            if (!currentRocket!!.dysonCargo)
                return 1
        }
        //Todo limit some kinds of cargo to one per slot
        //current dyson cargo is 16 per cargo element, max rocket is 6 modules * 8 storage slots * 16 items = 768 max dyson parts per rocket
        return 16//default
    }

    val ownerShip = OwnerShipTileEntityComponent()
    val dynCapability = DynamicFluidCapability(0, mutableListOf("rocket_fuel"))
    val fluidBuffer = FluidTileEntityComponent(dynCapability, EnumFacing.values().toMutableSet())
    val inventoryBuffer = InventoryTileEntityComponent(0, this, EnumFacing.values().toMutableSet())

    var currentRocket: EntityRocket? = null

    fun linkToCurrentRocket(rocket: EntityRocket): Boolean {
        if (currentRocket == null || currentRocket!!.isDead) {
            currentRocket = rocket

            //forward capability to entity
            fluidBuffer.fluid = rocket.tank.fluid

            inventoryBuffer.inventory.stacks = rocket.cargoSlots!!
            inventoryBuffer.inventory.forceSlotTypes(DynamicInventoryCapability.InventoryType.BOTH)

            return true
        }
        return false
    }

    var buffer: Framebuffer? = null
    var crtBuffer: Framebuffer? = null
    var depthShader: BasicShaderProgram? = null
    lateinit var Ufarplane: BasicShaderProgram.ShaderUniform
    lateinit var UInSize: BasicShaderProgram.ShaderUniform

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


    override fun getGui(player: EntityPlayer?): TCGui {

        val gui = TCGui(guiHeight = 230, container = TCContainer(1, 1))
        gui.registerTab(object : TCTab("${getBlockType().localizedName} linked: ${currentRocket != null}", gui, -1, TCIcon(this.blockType)) {

            override fun init() {

                val fm = DefaultFluidMeter(10, 25, fluidBuffer, gui)
                fm.width = 20
                fm.height = 105

                components.add(fm)

                if (player != null) {
                    //stick to bottom
                    addPlayerInventorySlots(player, 8, gui.origHeight - 58 - 16 - 5 - 12)
                }
            }
        })

        gui.registerTab(object : TCTab("uwu", gui, -1) {

            val ic = Interpolator.InterpolationCycle<Interpolator.PosLook>()
            val gridPolater = Interpolator.InterpolationCycle<Interpolator.InterpolateFloat>()
            var crtShader: ShaderGroup? = null

            var counter = 1.0

            val pointsSmall = mutableListOf<Vec3d>()
            val pointsMedium = mutableListOf<Vec3d>()
            val pointsBig = mutableListOf<Vec3d>()

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

                val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
                val sW = scaledResolution.scaledWidth_double
                val sH = scaledResolution.scaledHeight_double

                super.update()
            }

            override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

                GlStateManager.pushMatrix()
                val mc = Minecraft.getMinecraft()

                val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
                val sW = scaledResolution.getScaledWidth_double()
                val sH = scaledResolution.getScaledHeight_double()

                GlStateManager.color(1f, 1f, 1f, 1f)
                mc.fontRenderer.drawString(name + " " + zoom + " " + min(5f * (zoom * 10), 5f), 8f + x, 8f + y, 4210752, false)

                val tess = Tessellator.getInstance()
                val tessBuff = tess.buffer

                if (depthShader == null) {
                    basicCrtShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/default.vsh"), ResourceLocation("technocracy.astronautics", "shader/crt.fsh"))
                    basicCrtShader.start()
                    UConvergeX = basicCrtShader.getUniform("ConvergeX", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3).uploadUniform(-1.0f, 0.0f, 0.5f)
                    UConvergeY = basicCrtShader.getUniform("ConvergeY", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_3).uploadUniform(0.0f, -1.0f, 0.5f)
                    UhardScan = basicCrtShader.getUniform("hardScan", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(-3.0f)
                    Uwarp = basicCrtShader.getUniform("warp", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2).uploadUniform(1.0f / 16.0f, 1.0f / 16.0f)
                    UmaskDark = basicCrtShader.getUniform("maskDark", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1f)
                    UmaskLight = basicCrtShader.getUniform("maskLight", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1f)
                    Uscalar = basicCrtShader.getUniform("scalar", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2).uploadUniform(6f, 6f)
                    Usaturation = basicCrtShader.getUniform("saturation", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(1.8f)
                    UpixelScaler = basicCrtShader.getUniform("pixelScaler", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1).uploadUniform(2f)
                    basicCrtShader.getUniform("sampler", BasicShaderProgram.ShaderUniform.UniformType.SAMPLER).uploadUniform(0)
                    basicCrtShader.stop()

                    depthShader = BasicShaderProgram(ResourceLocation("technocracy.astronautics", "shader/logdepth.vsh"), ResourceLocation("technocracy.astronautics", "shader/logdepth.fsh"))
                    depthShader!!.start()
                    Ufarplane = depthShader!!.getUniform("farplane", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_1)
                    UInSize = depthShader!!.getUniform("InSize", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)
                } else {
                    depthShader!!.start()
                }

                val last = buffer
                crtBuffer = crtBuffer.validateAndClear()
                buffer = buffer.validateAndClear()
                buffer?.setFramebufferColor(0.12f, 0.12f, 0.12f, 0f)

                if (crtShader == null || last != buffer) {
                    crtShader?.deleteShaderGroup()

                    crtShader = ShaderGroup(mc.textureManager, mc.resourceManager, buffer, ResourceLocation("technocracy.astronautics", "shader/crt.json"))
                    crtShader!!.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
                }

                val playerPos = ic.getInterpolated(this.zoom)

                setupCameraTransform(playerPos)


                if (pointsSmall.isEmpty()) {
                    val r = kotlin.random.Random(12147)
                    //size of farplane for now
                    val xyz = 800_000f

                    val perList = 2000 / 3

                    for (i in 0..perList) {
                        val yaw = Math.toRadians(MathHelper.wrapDegrees(r.nextInt()).toDouble())
                        val pitch = Math.toRadians(MathHelper.wrapDegrees(r.nextInt()).toDouble())
                        pointsSmall.add(Vec3d(cos(pitch) * sin(yaw) * xyz, sin(pitch) * xyz, cos(pitch) * cos(yaw) * xyz))
                    }
                    for (i in 0..perList) {
                        val yaw = Math.toRadians(MathHelper.wrapDegrees(r.nextInt()).toDouble())
                        val pitch = Math.toRadians(MathHelper.wrapDegrees(r.nextInt()).toDouble())
                        pointsMedium.add(Vec3d(cos(pitch) * sin(yaw) * xyz, sin(pitch) * xyz, cos(pitch) * cos(yaw) * xyz))
                    }
                    for (i in 0..(perList * 0.5).toInt()) {
                        val yaw = Math.toRadians(MathHelper.wrapDegrees(r.nextInt()).toDouble())
                        val pitch = Math.toRadians(MathHelper.wrapDegrees(r.nextInt()).toDouble())
                        pointsBig.add(Vec3d(cos(pitch) * sin(yaw) * xyz, sin(pitch) * xyz, cos(pitch) * cos(yaw) * xyz))
                    }
                }

                GlStateManager.enableDepth()
                GL11.glDepthMask(true)
                GlStateManager.disableAlpha()
                GlStateManager.disableTexture2D()
                GlStateManager.disableCull()
                GlStateManager.shadeModel(7425)
                GlStateManager.glLineWidth(2f)

                GL11.glEnable(GL11.GL_POINT_SMOOTH)
                GL11.glPointSize(1f)

                //apply rotation
                GlStateManager.rotate(playerPos.look.z, 0.0f, 0.0f, 1.0f)
                GlStateManager.rotate(playerPos.look.y, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(playerPos.look.x, 0.0f, 1.0f, 0.0f)

                GL11.glBegin(GL11.GL_POINTS)
                for (p in pointsSmall) {
                    GL11.glVertex3d(p.x, p.y, p.z)
                }
                GL11.glEnd()
                GL11.glPointSize(1.5f)
                GL11.glBegin(GL11.GL_POINTS)
                for (p in pointsMedium) {
                    GL11.glVertex3d(p.x, p.y, p.z)
                }
                GL11.glEnd()
                GL11.glPointSize(4f)
                GL11.glBegin(GL11.GL_POINTS)
                for (p in pointsBig) {
                    GL11.glVertex3d(p.x, p.y, p.z)
                }
                GL11.glEnd()

                //remove rotation
                GlStateManager.rotate(-playerPos.look.x, 0.0f, 1.0f, 0.0f)
                GlStateManager.rotate(-playerPos.look.y, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(-playerPos.look.z, 0.0f, 0.0f, 1.0f)

                //translate and then rotate
                GlStateManager.translate(-playerPos.pos.x, -playerPos.pos.y, -playerPos.pos.z)
                GlStateManager.rotate(playerPos.look.z, 0.0f, 0.0f, 1.0f)
                GlStateManager.rotate(playerPos.look.y, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(playerPos.look.x, 0.0f, 1.0f, 0.0f)

                val sizeEarth = 256

                //earth
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

                GlStateManager.rotate(76f, 0f, 1f, 0f)

                depthShader!!.stop()

                mc.entityRenderer.setupOverlayRendering()

                basicCrtShader.start()
                Uscalar.uploadUniform(12f, 12f)
                UmaskDark.uploadUniform(0.5f)
                UmaskLight.uploadUniform(1f)
                UhardScan.uploadUniform(-2f)
                Usaturation.uploadUniform(4f)
                UpixelScaler.uploadUniform(1.5f)
                UConvergeX.uploadUniform(-1f,0f,1f)
                UConvergeY.uploadUniform(0f,-1f,1f)
                basicCrtShader.updateUniforms()

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

            fun drawTopBottom(factor: Float, size: Float, color: Vector4f) {
                val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
                val sizei = size.toInt()
                var height = size

                GL11.glBegin(GL11.GL_LINES)
                for (unused in 0..1) {
                    var b = true
                    for (i in -sizei..sizei) {
                        if (i != -sizei && i != sizei && i % test != 0) continue
                        GlStateManager.color(color.x, color.y, color.z, if (!b) color.w else 1f)
                        b = !b
                        GL11.glVertex3f(i.toFloat(), height, -size)
                        GL11.glVertex3f(i.toFloat(), height, size)
                        GL11.glVertex3f(-size, height, i.toFloat())
                        GL11.glVertex3f(size, height, i.toFloat())
                    }
                    height *= -1
                }
                GL11.glEnd()
            }

            fun drawSide(factor: Float, size: Float, color: Vector4f) {
                val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
                val sizei = size.toInt()
                var height = size

                GL11.glBegin(GL11.GL_LINES)
                for (unused in 0..1) {
                    var b = true
                    for (i in -sizei..sizei) {
                        if (i != -sizei && i != sizei && i % test != 0) continue
                        GlStateManager.color(color.x, color.y, color.z, if (!b) color.w else 1f)
                        b = !b
                        GL11.glVertex3f(height, i.toFloat(), -size)
                        GL11.glVertex3f(height, i.toFloat(), size)
                        GL11.glVertex3f(height, -size, i.toFloat())
                        GL11.glVertex3f(height, size, i.toFloat())
                    }
                    height *= -1
                }
                GL11.glEnd()
            }

            fun drawFace(factor: Float, size: Float, color: Vector4f) {
                val test = if (factor == 0f) 1 else (size * 2 / factor).roundToInt()
                val sizei = size.toInt()
                var height = size

                GL11.glBegin(GL11.GL_LINES)
                for (unused in 0..1) {
                    var b = true
                    for (i in -sizei..sizei) {
                        if (i != -sizei && i != sizei && i % test != 0) continue
                        GlStateManager.color(color.x, color.y, color.z, if (!b) color.w else 1f)
                        b = !b
                        GL11.glVertex3f(i.toFloat(), -size, height)
                        GL11.glVertex3f(i.toFloat(), size, height)
                        GL11.glVertex3f(-size, i.toFloat(), height)
                        GL11.glVertex3f(size, i.toFloat(), height)
                    }
                    height *= -1
                }
                GL11.glEnd()
            }

            fun drawCube(size: Int, r: Float, g: Float, b: Float, posCube: Vector3f, posPlayer: Vector3f, tilt: Vector3f = Vector3f(0f, 0f, 0f), hasRings: Boolean = false) {

                GlStateManager.translate(posCube.x, posCube.y, posCube.z)
                GlStateManager.rotate(tilt.z, 0.0f, 0.0f, 1.0f)
                GlStateManager.rotate(tilt.y, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(tilt.x, 0.0f, 1.0f, 0.0f)

                //use rough distance for performance and quick checking
                val distance = sqrt((posPlayer.x - posCube.x).pow(2f) + (posPlayer.y - posCube.y).pow(2f) + (posPlayer.z - posCube.z).pow(2f)) - size - 1.6f

                val currentGrid = gridPolater.getCurrent(distance)
                val nextGrid = gridPolater.getNext(distance)
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

                val alpha = if (nextGrid == null) 1f else 1f - Interpolator.linearInterpolate(0.0f, 1f, currentGrid.time, nextGrid.time, distance)
                val color = Vector4f(r, g, b, alpha)

                drawTopBottom(currentScale, sizef, color)
                drawSide(currentScale, sizef, color)
                drawFace(currentScale, sizef, color)

                GlStateManager.rotate(-tilt.x, 0.0f, 1.0f, 0.0f)
                GlStateManager.rotate(-tilt.y, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(-tilt.z, 0.0f, 0.0f, 1.0f)
                GlStateManager.translate(-posCube.x, -posCube.y, -posCube.z)
            }

            fun setupCameraTransform(playerPos: Interpolator.PosLook) {
                val mc = Minecraft.getMinecraft()
                GlStateManager.matrixMode(5889)
                GlStateManager.loadIdentity()

                val farPlaneDistance = 500_000f
                Project.gluPerspective(90f, mc.displayWidth.toFloat() / mc.displayHeight.toFloat(), 0.005f, 200_000f)
                GlStateManager.matrixMode(5888)
                GlStateManager.loadIdentity()


                Ufarplane.uploadUniform(farPlaneDistance)
                UInSize.uploadUniform(1f, 1f)
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
                ic.clear()
                //add offset to prevent clipping
                ic.addStep(Interpolator.PosLook(Vector3f(0f, 1.62f + 0.25f, 0f), Vector3f(0f, 5f, 0f)), 0f)
                ic.addStep(Interpolator.PosLook(Vector3f(0f, 16.2f, 0f), Vector3f(0f, 5f, 0f)), 10f)
                ic.addStep(Interpolator.PosLook(Vector3f(0f, 126.2f, 100f), Vector3f(0f, 0f, 0f)), 20f)
                //ic.addStep(Interpolator.PosLook(Vector3f(100f, 0f, 8000f), Vector3f(-15f, 0f, 0f)), 40f)
                ic.addStep(Interpolator.PosLook(Vector3f(00f, 0f, 8000f), Vector3f(180f, 0f, 0f)), 40f)
                ic.addStep(Interpolator.PosLook(Vector3f(0f, 0f, 8000f), Vector3f(360f, 0f, 0f)), 60f)
                ic.addStep(Interpolator.PosLook(Vector3f(7800f, 1.62f + 0.25f - 128, 0f), Vector3f(360f, 5f, 0f)), 70f)

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
        })

        return gui
    }

    fun render() {

    }

    fun unlinkRocket() {
        currentRocket = null
        fluidBuffer.fluid = dynCapability
        inventoryBuffer.inventory.stacks = NonNullList.withSize(0, ItemStack.EMPTY)
    }

    init {
        registerComponent(ownerShip, "ownership")
        registerComponent(fluidBuffer, "fluidBuffer")
        registerComponent(inventoryBuffer, "invBuffer")
        //no need to save or update as it only references to the entity
        fluidBuffer.allowAutoSave = false
        inventoryBuffer.allowAutoSave = false
    }
}