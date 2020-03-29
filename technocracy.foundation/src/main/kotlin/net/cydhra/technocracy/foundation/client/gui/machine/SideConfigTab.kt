package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.ITCComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCCapabilityComponent
import net.cydhra.technocracy.foundation.client.gui.components.button.DefaultButton
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.EnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.CoolantMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.FluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotPlayer
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.items.wrenchItem
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractCapabilityTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractDirectionalCapabilityTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.model.components.IComponent
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.componentsync.ClientChangeSideConfigPacket
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.cydhra.technocracy.foundation.util.opengl.ScreenspaceUtil
import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.cydhra.technocracy.foundation.util.structures.TemplateClientWorld
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.MinecraftForgeClient
import org.apache.commons.lang3.text.WordUtils
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import kotlin.math.min


class SideConfigTab(parent: TCGui, val machine: MachineTileEntity, val mainTab: TCTab) : TCTab("SideConfig", parent, icon = TCIcon(wrenchItem)) {

    companion object {
        var framebuffer: Framebuffer? = null
        lateinit var shader: BasicShaderProgram
        var isInit = false
    }


    lateinit var infoTitleLabel: DefaultLabel
    lateinit var hideBlocks: DefaultButton

    override fun init() {

        val height = 58 + 18
        val offsetY = parent.guiHeight - height - 4

        infoTitleLabel = DefaultLabel(10, offsetY + 2, "")

        hideBlocks = DefaultButton(10, offsetY + height - 15 - 2, 15, 15, "H") { _, _, button ->
            if (button == 0)
                hideNeighbors = !hideNeighbors
            hideBlocks.text = if (hideNeighbors) "S" else "H"
        }

        components.add(infoTitleLabel)
        components.add(hideBlocks)
    }

    var hideNeighbors = false

    var lastSideHit: EnumFacing? = null
    var currentLockedSide = EnumFacing.NORTH

    var yaw = -180f
    var pitch = 0f
    var zoomLevel: Float = -2f

    var checkMouse = false

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            checkMouse = true

            mainTab.components.forEach {
                if (it !is TCSlotPlayer) {
                    var face = lastSideHit ?: currentLockedSide
                    //if (face.axis.isHorizontal)
                        //face = face.rotateY().rotateY()

                    var added = false
                    var changedComponent: IComponent? = null

                    when (it) {
                        is EnergyMeter -> {
                            if (it.isMouseOnComponent(mouseX - x, mouseY - y)) {
                                changedComponent = it.component
                                if (!it.component.facing.remove(face)) {
                                    it.component.facing.add(face)
                                    added = true
                                }
                            }
                        }
                        is CoolantMeter -> {

                            if (it.meterIn.isMouseOnComponent(mouseX - x - it.posX, mouseY - y - it.posY)) {
                                changedComponent = it.component
                                if (!it.coolantIn.facing.remove(face)) {
                                    it.coolantIn.facing.add(face)
                                    added = true
                                }
                            }
                            if (it.meterOut.isMouseOnComponent(mouseX - x - it.posX, mouseY - y - it.posY)) {
                                changedComponent = it.component
                                if (!it.coolantOut.facing.remove(face)) {
                                    it.coolantOut.facing.add(face)
                                    added = true
                                }
                            }
                        }
                        is FluidMeter -> {
                            if (it.isMouseOnComponent(mouseX - x, mouseY - y)) {
                                changedComponent = it.component
                                if (!it.component.facing.remove(face)) {
                                    it.component.facing.add(face)
                                    added = true
                                }
                            }
                        }
                        is TCSlotIO -> {
                            val handler = it.itemHandler
                            if (handler is DynamicInventoryCapability) {
                                val comp = handler.componentParent
                                if (comp is InventoryTileEntityComponent) {
                                    if (it.isMouseOnComponent(mouseX - x, mouseY - y)) {
                                        changedComponent = comp
                                        if (!comp.facing.remove(face)) {
                                            comp.facing.add(face)
                                            added = true
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (changedComponent != null) {
                        val search = machine.getComponents().find { it.second == changedComponent }
                        if (search != null) {
                            PacketHandler.sendToServer(ClientChangeSideConfigPacket(search.first, face, added))
                        }
                    }
                }
            }
        }

        super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

        if (!isInit) {
            isInit = true
            shader = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/fade.vsh"), ResourceLocation("technocracy.foundation", "shaders/fade.fsh"))
        }

        if (Mouse.isButtonDown(0)) {
            yaw += Mouse.getDX()
            pitch -= Mouse.getDY()
        } else {
            if (checkMouse) {
                checkMouse = false
                currentLockedSide = lastSideHit ?: currentLockedSide
            }
        }

        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())


        GlStateManager.pushAttrib()
        GlStateManager.pushMatrix()
        framebuffer?.setFramebufferColor(0f, 0f, 0f, 1f)
        framebuffer = framebuffer.validateAndClear((parent.guiWidth - 7 - 7) * scaledResolution.scaleFactor, (58 + 18) * scaledResolution.scaleFactor)

        val mc = Minecraft.getMinecraft()
        GlStateManager.matrixMode(5889)
        GlStateManager.loadIdentity()
        Project.gluPerspective(70f, (parent.guiWidth - 7 - 7) / (58 + 18f), 0.01f, 100f)
        GlStateManager.matrixMode(5888)
        GlStateManager.loadIdentity()
        GlStateManager.disableAlpha()
        GlStateManager.enableCull()
        GlStateManager.shadeModel(7425)
        GlStateManager.enableTexture2D()

        val state = machine.world.getBlockState(machine.pos).getActualState(machine.world, machine.pos)
        val pos = machine.pos

        val tcw = TemplateClientWorld(mc.world, mutableListOf(BlockInfo(BlockPos(0, 0, 0), state)), pos)

        val tess = Tessellator.getInstance()
        val bufferBuilder = tess.buffer


        GL11.glTranslated(0.0, 0.0, zoomLevel.toDouble())
        GL11.glRotated((this.pitch).toDouble(), 1.0, 0.0, 0.0)
        GL11.glRotated((this.yaw).toDouble(), 0.0, 1.0, 0.0)
        GL11.glTranslated(-0.5, -0.5, -0.5)

        ScreenspaceUtil.initMatrix()

        val width = parent.guiWidth - 7 - 7
        val height = 58 + 18
        val offsetX = x + 7
        val offsetY = y + parent.guiHeight - height - 4


        val vecs = ScreenspaceUtil.getPositonsOnFrustum(Mouse.getX() - offsetX * scaledResolution.scaleFactor, Mouse.getY() - (parent.height - y - parent.guiHeight + 4) * scaledResolution.scaleFactor)
        val rayTrace = tcw.rayTraceBlocks(vecs[0], vecs[1], false, false, false)

        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        val model = mc.blockRendererDispatcher.getModelForState(state)

        bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)
        mc.blockRendererDispatcher.blockModelRenderer.renderModelFlat(tcw, model, state.block.getExtendedState(state, machine.world, machine.pos), BlockPos(0, 0, 0), bufferBuilder, false, 0)
        tess.draw()

        GlStateManager.enableAlpha()
        GlStateManager.disableDepth()

        if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            lastSideHit = rayTrace.sideHit
            infoTitleLabel.text = WordUtils.capitalize(lastSideHit.toString())

            val opposite = rayTrace.sideHit.opposite
            var bb = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
            bb = bb.contract(opposite.frontOffsetX.toDouble(), opposite.frontOffsetY.toDouble(), opposite.frontOffsetZ.toDouble())
            Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("technocracy.foundation", "textures/gui/sideconfig/selection.png"))
            OpenGLBoundingBox.drawTexturedBoundingBox(bb)

        } else {
            lastSideHit = null
            infoTitleLabel.text = "None"
        }

        if (lastSideHit != currentLockedSide) {
            val opposite = currentLockedSide.opposite
            var bb = AxisAlignedBB(-0.0, -0.0, -0.0, 1.0, 1.0, 1.0)
            bb = bb.contract(opposite.frontOffsetX.toDouble(), opposite.frontOffsetY.toDouble(), opposite.frontOffsetZ.toDouble())
            Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("technocracy.foundation", "textures/gui/sideconfig/selection.png"))
            GlStateManager.color(0f, 0.6f, 0.8f)
            OpenGLBoundingBox.drawTexturedBoundingBox(bb)
        }

        GlStateManager.enableDepth()

        val rotations = Vector3f(0f, 0f, 0f)
        val colorMap = mutableMapOf<AbstractCapabilityTileEntityComponent, Int>()

        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)
        for (f in EnumFacing.values()) {
            rotations.y = 0f
            rotations.z = 0f

            var face = f
            val opposite = face.opposite
            val bb = AxisAlignedBB(-0.0, -0.0, -0.0, 1.0, 1.0, 1.0).contract(opposite.frontOffsetX.toDouble(), opposite.frontOffsetY.toDouble(), opposite.frontOffsetZ.toDouble()).offset(opposite.frontOffsetX.toDouble() * -0.01, opposite.frontOffsetY.toDouble() * -0.01, opposite.frontOffsetZ.toDouble() * -0.01)


            val visited = mutableSetOf<AbstractCapabilityTileEntityComponent>()
            val totalRotsOnSide = Vector2f(0f, 0f)

            //increase rotation vector based on component type
            val increaseRotation: (AbstractDirectionalCapabilityTileEntityComponent) -> Unit = { comp: AbstractDirectionalCapabilityTileEntityComponent ->
                if (visited.add(comp) && comp.facing.contains(face)) {
                    if (comp.getDirection() == AbstractDirectionalCapabilityTileEntityComponent.Direction.OUTPUT) {
                        totalRotsOnSide.y++
                    } else {
                        totalRotsOnSide.x++
                    }
                }
            }

            //calculate max elements on side, used to color in the right amount of elements
            for (it in mainTab.components) {
                if (it is TCCapabilityComponent<*>) {
                    if (it.component is AbstractDirectionalCapabilityTileEntityComponent) {
                        increaseRotation(it.component)
                    }
                } else if (it !is TCSlotPlayer) {
                    when (it) {
                        is CoolantMeter -> {
                            increaseRotation(it.coolantIn)
                            increaseRotation(it.coolantOut)
                        }

                        is TCSlotIO -> {
                            val handler = it.itemHandler
                            if (handler is DynamicInventoryCapability) {
                                val comp = handler.componentParent
                                if (comp is InventoryTileEntityComponent) {
                                    increaseRotation(comp)
                                }
                            }
                        }
                    }
                }
            }

            visited.clear()

            for (it in mainTab.components) {
                if (it is TCCapabilityComponent<*>) {
                    if (it.component is AbstractDirectionalCapabilityTileEntityComponent) {
                        if (visited.add(it.component))
                            renderBlockOverlay(face, it.component, colorMap, bb, rotations, totalRotsOnSide)
                    }
                } else if (it !is TCSlotPlayer) {
                    when (it) {
                        is CoolantMeter -> {
                            if (visited.add(it.coolantIn))
                                renderBlockOverlay(face, it.coolantIn, colorMap, bb, rotations, totalRotsOnSide)
                            if (visited.add(it.coolantOut))
                                renderBlockOverlay(face, it.coolantOut, colorMap, bb, rotations, totalRotsOnSide)
                        }

                        is TCSlotIO -> {
                            val handler = it.itemHandler
                            if (handler is DynamicInventoryCapability) {
                                val comp = handler.componentParent
                                if (comp is InventoryTileEntityComponent && visited.add(comp)) {
                                    renderBlockOverlay(face, comp, colorMap, bb, rotations, totalRotsOnSide)
                                }
                            }
                        }
                    }
                }
            }
        }

        //render the other blocks
        if (!hideNeighbors) {
            Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            GlStateManager.enableCull()
            RenderHelper.disableStandardItemLighting()
            mc.entityRenderer.disableLightmap()
            GlStateManager.disableLighting();
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)
            shader.start()

            GlStateManager.pushMatrix()
            GlStateManager.disableAlpha()
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID)
            renderBlocks(BlockRenderLayer.SOLID, pos, tess, tcw)

            //ForgeHooksClient.setRenderPass(0)
            //renderTileEntitys(pos, machine.world)
            //Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            //GlStateManager.alphaFunc(516, 0.5f)
            //GlStateManager.enableAlpha()

            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT_MIPPED)
            renderBlocks(BlockRenderLayer.CUTOUT_MIPPED, pos, tess, tcw)
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT)
            renderBlocks(BlockRenderLayer.CUTOUT, pos, tess, tcw)



            GlStateManager.matrixMode(5888)
            GlStateManager.popMatrix()
            GlStateManager.pushMatrix()
            RenderHelper.enableStandardItemLighting()
            ForgeHooksClient.setRenderPass(0)
            GlStateManager.depthMask(false)
            renderTileEntitys(pos, machine.world)
            GlStateManager.depthMask(true)
            ForgeHooksClient.setRenderPass(0)
            RenderHelper.disableStandardItemLighting()
            Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GlStateManager.matrixMode(5888)
            GlStateManager.popMatrix()

            GlStateManager.depthMask(false)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)
            //GlStateManager.alphaFunc(516, 0.1f)
            GlStateManager.shadeModel(7425)

            ForgeHooksClient.setRenderLayer(BlockRenderLayer.TRANSLUCENT)
            renderBlocks(BlockRenderLayer.TRANSLUCENT, pos, tess, tcw)

            RenderHelper.enableStandardItemLighting()
            ForgeHooksClient.setRenderPass(1)
            renderTileEntitys(pos, machine.world)
            GlStateManager.depthMask(true)
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            ForgeHooksClient.setRenderPass(-1)
            RenderHelper.disableStandardItemLighting()

            //ForgeHooksClient.setRenderPass(1)
            //renderTileEntitys(pos, machine.world)

            //ForgeHooksClient.setRenderPass(-1)

            shader.stop()

            ForgeHooksClient.setRenderLayer(null)
        }


        GlStateManager.color(1f, 1f, 1f, 1f)

        bufferBuilder.setTranslation(0.0, 0.0, 0.0)

        GlStateManager.popMatrix()
        GlStateManager.popAttrib()


        mc.entityRenderer.setupOverlayRendering()

        framebuffer?.bindFramebufferTexture()
        mc.framebuffer.bindFramebuffer(true)


        GlStateManager.translate(offsetX.toDouble(), offsetY.toDouble(), 0.0)
        GlStateManager.enableTexture2D()
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder.pos(0.0, 0.0, 0.0).tex(0.0, 1.0).endVertex()
        bufferBuilder.pos(0.0, height.toDouble(), 0.0).tex(0.0, 0.0).endVertex()
        bufferBuilder.pos(width.toDouble(), height.toDouble(), 0.0).tex(1.0, 0.0).endVertex()
        bufferBuilder.pos(width.toDouble(), 0.0, 0.0).tex(1.0, 1.0).endVertex()
        tess.draw()
        GlStateManager.translate(-offsetX.toDouble(), -offsetY.toDouble(), 0.0)

        //render our components
        this.components.forEach {
            it.draw(x, y, mouseX, mouseY, partialTicks)
        }

        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(mainTab.name, 8f + x, 8f + y, 4210752, false)

        GlStateManager.enableDepth()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)

        var face = lastSideHit ?: currentLockedSide

        //draw selection boxes and the components of the maintab
        loop@ for (it in mainTab.components) {
            if (it !is TCSlotPlayer) {
                if (it is CoolantMeter) {
                    it.drawBackground(x, y)

                    val pX = x + it.posX
                    val pY = y + it.posY

                    renderSelectionOutline(pX, pY, face, it.meterIn, it.coolantIn, colorMap, rotations)
                    renderSelectionOutline(pX, pY, face, it.meterOut, it.coolantOut, colorMap, rotations)

                    it.draw(x, y, mouseX, mouseY, -1f)
                    continue@loop
                } else if (it is TCCapabilityComponent<*>) {
                    if (it.component is AbstractDirectionalCapabilityTileEntityComponent) {
                        @Suppress("UNCHECKED_CAST")
                        renderSelectionOutline(x, y, face, it, it.component, colorMap, rotations)
                    }
                } else {
                    when (it) {
                        is TCSlotIO -> {
                            val handler = it.itemHandler
                            if (handler is DynamicInventoryCapability) {
                                val comp = handler.componentParent
                                if (comp is InventoryTileEntityComponent) {
                                    renderSelectionOutline(x - 1, y - 1, face, it, comp, colorMap, rotations)
                                }
                            }
                        }
                    }
                }
                it.draw(x, y, mouseX, mouseY, partialTicks)
            }
        }

        //render the items in the slots
        mc.renderItem.zLevel = 100.0f
        mainTab.components.forEach {
            if (it is TCSlotIO) {
                val slot = parent.container.inventorySlots[it.slotNumber]
                mc.renderItem.renderItemAndEffectIntoGUI(mc.player, slot.stack, slot.xPos + parent.guiX, slot.yPos + parent.guiY)
                mc.renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, slot.stack, slot.xPos + parent.guiX, slot.yPos + parent.guiY, null)
            }
        }
        mc.renderItem.zLevel = 0.0f
    }

    fun renderSelectionOutline(x: Int, y: Int, facing: EnumFacing, renderComponent: ITCComponent, component: AbstractDirectionalCapabilityTileEntityComponent, colorMap: MutableMap<AbstractCapabilityTileEntityComponent, Int>, rotation: Vector3f) {
        val color = colorMap.getOrPut(component) {
            val color = Color.getHSBColor(rotation.x / 360f, 1f, 1f).rgb
            rotation.x += 30
            color
        }

        if (component.facing.contains(facing)) {
            Gui.drawRect(x + renderComponent.posX - 1, y + renderComponent.posY - 1, x + renderComponent.posX + renderComponent.width + 1, y + renderComponent.posY + renderComponent.height + 1, color)
        } else {

            val r = (color shr 16 and 255).toFloat() / 255.0f
            val g = (color shr 8 and 255).toFloat() / 255.0f
            val b = (color and 255).toFloat() / 255.0f

            Gui.drawRect(x + renderComponent.posX - 1, y + renderComponent.posY - 1, x + renderComponent.posX + renderComponent.width + 1, y + renderComponent.posY + renderComponent.height + 1, MathHelper.rgb(r, g, b) or (80 shl 24))
        }
    }

    fun renderBlockOverlay(facing: EnumFacing, component: AbstractDirectionalCapabilityTileEntityComponent, colorMap: MutableMap<AbstractCapabilityTileEntityComponent, Int>, bb: AxisAlignedBB, rotation: Vector3f, totalRotsOnSide: Vector2f): Vector3f {
        val color = colorMap.getOrPut(component) {
            val color = Color.getHSBColor(rotation.x / 360f, 1f, 1f).rgb
            rotation.x += 30
            color
        }


        if (component.facing.contains(facing)) {
            var amount = 4

            val image: String

            var rot = if (component.getDirection() == AbstractDirectionalCapabilityTileEntityComponent.Direction.OUTPUT) {
                image = "out"

                if (totalRotsOnSide.y == 2f) {
                    amount = 2
                } else if (totalRotsOnSide.y >= 3f) {
                    amount = 1
                }

                rotation.z++
            } else {
                image = "in"

                if (totalRotsOnSide.x == 2f) {
                    amount = 2
                } else if (totalRotsOnSide.x >= 3f) {
                    amount = 1
                }

                rotation.y++
            }

            for (i in 0 until amount) {
                Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("technocracy.foundation", "textures/gui/sideconfig/${image}_${(rot % 4).toInt()}.png"))
                OpenGLBoundingBox.drawTexturedBoundingBox(bb, color = color)
                if (amount == 2) {
                    rot += 2
                } else {
                    rot++
                }
            }
        }
        return rotation
    }

    fun renderTileEntitys(pos: BlockPos, tcw: World) {
        val mc = Minecraft.getMinecraft()

        //todo fix mekanism pipes beeing rendered at the wrong positions

        for (face in EnumFacing.values()) {
            val offset = pos.offset(face)
            var tile = tcw.getTileEntity(offset);
            if (tile != null) {
                if (tile.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) {
                    var offX = 0
                    var offZ = 0
                    if (tile is TileEntityChest) {
                        if (tile.adjacentChestXNeg != null) {
                            tile = tile.adjacentChestXNeg!!
                            offX = -1
                        } else if (tile.adjacentChestZNeg != null) {
                            tile = tile.adjacentChestZNeg!!
                            offZ = -1
                        }
                    }
                    GlStateManager.pushMatrix()
                    GlStateManager.translate((face.frontOffsetX + offX).toDouble(), face.frontOffsetY.toDouble(), (face.frontOffsetZ + offZ).toDouble())
                    TileEntityRendererDispatcher.instance.render(tile, 0.0, 0.0, 0.0, mc.renderPartialTicks)
                    GlStateManager.popMatrix()
                }
            }
        }
    }

    override fun handleMouseInput() {
        val i = Mouse.getEventDWheel()
        if (i > 0)
            zoomLevel += 0.3f
        else if (i < 0)
            zoomLevel -= 0.3f

        zoomLevel = min(-0.3f, zoomLevel)
    }

    fun renderBlocks(layer: BlockRenderLayer, pos: BlockPos, tess: Tessellator, tcw: World) {
        val mc = Minecraft.getMinecraft()
        val dist = 1.0
        for (face in EnumFacing.values()) {
            val offset = pos.offset(face)
            val state = mc.world.getBlockState(offset).getActualState(tcw, offset)
            if (!state.block.canRenderInLayer(state, layer) || state.renderType != EnumBlockRenderType.MODEL) continue
            val model = mc.blockRendererDispatcher.getModelForState(state)
            tess.buffer.begin(7, DefaultVertexFormats.BLOCK)
            tess.buffer.setTranslation(face.frontOffsetX * dist, face.frontOffsetY * dist, face.frontOffsetZ * dist)
            mc.blockRendererDispatcher.blockModelRenderer.renderModel(tcw, model, state.block.getExtendedState(state, tcw, offset), BlockPos(0, 0, 0), tess.buffer, false, 0)
            tess.draw()
        }
    }
}