package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.api.ecs.IComponent
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
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractTileEntityCapabilityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractTileEntityDirectionalCapabilityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractRotatableTileEntityBlock
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractRotatableTileEntityBlock.Companion.facingProperty
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.componentsync.ClientChangeSideConfigPacket
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.MultiTargetFBO
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.cydhra.technocracy.foundation.util.opengl.ScreenspaceUtil
import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.cydhra.technocracy.foundation.util.structures.TemplateClientWorld
import net.cydhra.technocracy.foundation.util.validate
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.BlockRenderLayer
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
import org.lwjgl.opengl.GL30
import org.lwjgl.util.glu.Project
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import kotlin.math.min

/**
 * A tab within technocracy-style GUIs that displays the side-configuration of machines and offers a way to change it.
 *
 * @param parent parent [TCGui] that owns this tab
 * @param machine the machine that is configured from this gui
 * @param mainTab the main tab of the machine, that is partially rendered into this gui.
 */
class SideConfigTab(parent: TCGui, val machine: MachineTileEntity, val mainTab: TCTab) : TCTab("SideConfig", parent, icon = TCIcon(wrenchItem)) {

    companion object {
        var framebuffer: Framebuffer? = null
        val shader = BasicShaderProgram(ResourceLocation("technocracy.foundation", "shaders/fade.vsh"), ResourceLocation("technocracy.foundation", "shaders/fade.fsh"))
        val alphaClip = shader.getUniform("alphaClip", BasicShaderProgram.ShaderUniform.UniformType.INT_1).uploadUniform(false)
        val screenSize = shader.getUniform("screenSize", BasicShaderProgram.ShaderUniform.UniformType.FLOAT_2)
    }


    lateinit var infoTitleLabel: DefaultLabel
    lateinit var hideBlocks: DefaultButton

    override fun init() {

        val height = 58 + 18
        val offsetY = parent.guiHeight - height - 4

        infoTitleLabel = DefaultLabel(10, offsetY + 2, "", gui = parent)

        hideBlocks = DefaultButton(10, offsetY + height - 15 - 2, 15, 15, "H", parent, "Hide side blocks") { _, _, _, button ->
            if (button == 0)
                hideNeighbors = !hideNeighbors
            hideBlocks.text = if (hideNeighbors) "S" else "H"
            hideBlocks.tooltip = if (hideNeighbors) "Show side blocks" else "Hide side blocks"
        }

        yaw = currentLockedSide.horizontalAngle

        components.add(infoTitleLabel)
        components.add(hideBlocks)
    }

    var hideNeighbors = false

    var lastSideHit: EnumFacing? = null
    var currentLockedSide = if (machine.blockType is AbstractRotatableTileEntityBlock) machine.getBlockState().getValue(facingProperty) else EnumFacing.NORTH

    var yaw = -180f
    var pitch = 0f
    var zoomLevel: Float = -2.6f

    var checkMouse = false
    var sharedFBO: MultiTargetFBO? = null

    override fun onClose() {
        sharedFBO?.deleteFramebuffer()
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            checkMouse = true

            mainTab.components.forEach {
                if (it !is TCSlotPlayer) {
                    val face = lastSideHit ?: currentLockedSide
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
                                changedComponent = it.coolantIn
                                if (!it.coolantIn.facing.remove(face)) {
                                    it.coolantIn.facing.add(face)
                                    added = true
                                }
                            }
                            if (it.meterOut.isMouseOnComponent(mouseX - x - it.posX, mouseY - y - it.posY)) {
                                changedComponent = it.coolantOut
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
                                if (comp is TileEntityInventoryComponent) {
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

    override fun update() {
        for (it in mainTab.components) {
            it.update()
        }
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {


        val mc = Minecraft.getMinecraft()

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

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
        GlStateManager.pushMatrix()
        framebuffer?.setFramebufferColor(0f, 0f, 0f, 0f)
        framebuffer = framebuffer.validateAndClear((parent.guiWidth - 7 - 7) * scaledResolution.scaleFactor, (58 + 18) * scaledResolution.scaleFactor)
        val framebuffer = framebuffer!!
        sharedFBO = sharedFBO.validate(framebuffer, ownDepth = true)
        val sharedFBO = sharedFBO!!
        sharedFBO.framebufferClear()
        framebuffer.bindFramebuffer(true)

        //reset projection
        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GlStateManager.loadIdentity()
        Project.gluPerspective(70f, ((parent.guiWidth - 7 - 7) * scaledResolution.scaleFactor) / ((58 + 18f) * scaledResolution.scaleFactor), 0.01f, 100f)

        //reset projection
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GlStateManager.loadIdentity()

        GlStateManager.disableAlpha()
        GlStateManager.enableCull()
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GlStateManager.enableTexture2D()

        val state = machine.world.getBlockState(machine.pos).getActualState(machine.world, machine.pos)
        val pos = machine.pos

        val tcw = TemplateClientWorld(mc.world, mutableListOf(BlockInfo(BlockPos.ORIGIN, state)), BlockPos.ORIGIN)
        for (face in EnumFacing.values()) {
            val p = pos.offset(face)
            tcw.blocks.add(BlockInfo(p, mc.world.getBlockState(p)))
        }

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

        val vecs = ScreenspaceUtil.getPositonsOnFrustum(Mouse.getX() - offsetX * scaledResolution.scaleFactor, Mouse.getY() - (scaledResolution.scaledHeight - y - parent.guiHeight + 4) * scaledResolution.scaleFactor)
        val rayTrace = tcw.rayTraceBlocks(vecs[0], vecs[1], false, false, false)

        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        renderMachine(state, tcw)

        /*for(layer in BlockRenderLayer.values()) {
            if(!state.block.canRenderInLayer(state, layer)) continue
            ForgeHooksClient.setRenderLayer(layer)
            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)
            mc.blockRendererDispatcher.blockModelRenderer.renderModelFlat(tcw, model, state.block.getExtendedState(state, machine.world, machine.pos), BlockPos(0, 0, 0), bufferBuilder, false, 0)
            tess.draw()
        }*/

        /*GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5f)
        GlStateManager.disableDepth()

        GlStateManager.enableDepth()

        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.disableAlpha()*/

        //render the other blocks
        if (!hideNeighbors) {
            renderNeighbors(sharedFBO, pos, tess, tcw, framebuffer)
        }

        ForgeHooksClient.setRenderLayer(null)
        ForgeHooksClient.setRenderPass(-1)

        renderMouseSelection(rayTrace)

        val rotations = Vector3f(0f, 0f, 0f)
        val colorMap = mutableMapOf<AbstractTileEntityCapabilityComponent, Int>()

        renderIOOverlay(rotations, colorMap)

        bufferBuilder.setTranslation(0.0, 0.0, 0.0)

        GlStateManager.depthMask(true)


        GlStateManager.color(1f, 1f, 1f, 1f)



        GlStateManager.popMatrix()

        drawSceneToGui(framebuffer, offsetX, offsetY, width, height)

        //render our components
        this.components.forEach {
            it.draw(x, y, mouseX, mouseY, partialTicks)
        }

        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(mainTab.name, 8f + x, 8f + y, 4210752, false)

        GlStateManager.enableDepth()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

        val face = lastSideHit ?: currentLockedSide

        drawComponentOverlay(x, y, face, colorMap, rotations, mouseX, mouseY, partialTicks)
    }

    private fun renderMachine(state: IBlockState, tcw: TemplateClientWorld) {
        val mc = Minecraft.getMinecraft()
        val tess = Tessellator.getInstance()
        val bufferBuilder = tess.buffer

        val model = mc.blockRendererDispatcher.getModelForState(state)

        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.disableAlpha()

        if (state.block.canRenderInLayer(state, BlockRenderLayer.SOLID)) {
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID)
            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)
            mc.blockRendererDispatcher.blockModelRenderer.renderModelFlat(tcw, model, state.block.getExtendedState(state, machine.world, machine.pos), BlockPos(0, 0, 0), bufferBuilder, false, 0)
            tess.draw()
        }

        if (state.block.canRenderInLayer(state, BlockRenderLayer.CUTOUT_MIPPED)) {
            GlStateManager.enableAlpha()
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5f)
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT_MIPPED)
            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)
            mc.blockRendererDispatcher.blockModelRenderer.renderModelFlat(tcw, model, state.block.getExtendedState(state, machine.world, machine.pos), BlockPos(0, 0, 0), bufferBuilder, false, 0)
            tess.draw()
        }
        if (state.block.canRenderInLayer(state, BlockRenderLayer.CUTOUT)) {
            GlStateManager.enableAlpha()
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5f)
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT)
            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)
            mc.blockRendererDispatcher.blockModelRenderer.renderModelFlat(tcw, model, state.block.getExtendedState(state, machine.world, machine.pos), BlockPos(0, 0, 0), bufferBuilder, false, 0)
            tess.draw()
        }

        RenderHelper.enableStandardItemLighting()
        ForgeHooksClient.setRenderPass(0)
        GlStateManager.depthMask(false)
        renderTileEntity(this.machine, BlockPos.ORIGIN)
        RenderHelper.disableStandardItemLighting()
        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.disableAlpha()
        GlStateManager.shadeModel(GL11.GL_SMOOTH)

        if (state.block.canRenderInLayer(state, BlockRenderLayer.TRANSLUCENT)) {
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.TRANSLUCENT)
            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)
            mc.blockRendererDispatcher.blockModelRenderer.renderModelFlat(tcw, model, state.block.getExtendedState(state, machine.world, machine.pos), BlockPos(0, 0, 0), bufferBuilder, false, 0)
            tess.draw()
        }

        RenderHelper.enableStandardItemLighting()

        ForgeHooksClient.setRenderPass(1)
        renderTileEntity(machine, BlockPos.ORIGIN)

        GlStateManager.depthMask(true)
        ForgeHooksClient.setRenderPass(-1)
        RenderHelper.disableStandardItemLighting()
    }

    private fun renderIOOverlay(rotations: Vector3f, colorMap: MutableMap<AbstractTileEntityCapabilityComponent, Int>) {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        for (f in EnumFacing.values()) {
            rotations.y = 0f
            rotations.z = 0f

            var face = f
            val opposite = face.opposite
            val bb = AxisAlignedBB(-0.0, -0.0, -0.0, 1.0, 1.0, 1.0).contract(opposite.frontOffsetX.toDouble(), opposite.frontOffsetY.toDouble(), opposite.frontOffsetZ.toDouble()).offset(opposite.frontOffsetX.toDouble() * -0.01, opposite.frontOffsetY.toDouble() * -0.01, opposite.frontOffsetZ.toDouble() * -0.01)


            val visited = mutableSetOf<AbstractTileEntityCapabilityComponent>()
            val totalRotsOnSide = Vector2f(0f, 0f)

            //increase rotation vector based on component type
            val increaseRotation: (AbstractTileEntityDirectionalCapabilityComponent) -> Unit = { comp: AbstractTileEntityDirectionalCapabilityComponent ->
                if (visited.add(comp) && comp.facing.contains(face)) {
                    if (comp.getDirection() == AbstractTileEntityDirectionalCapabilityComponent.Direction.OUTPUT) {
                        totalRotsOnSide.y++
                    } else {
                        totalRotsOnSide.x++
                    }
                }
            }

            //calculate max elements on side, used to color in the right amount of elements
            for (it in mainTab.components) {
                if (it is TCCapabilityComponent<*>) {
                    if (it.component is AbstractTileEntityDirectionalCapabilityComponent) {
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
                                if (comp is TileEntityInventoryComponent) {
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
                    if (it.component is AbstractTileEntityDirectionalCapabilityComponent) {
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
                                if (comp is TileEntityInventoryComponent && visited.add(comp)) {
                                    renderBlockOverlay(face, comp, colorMap, bb, rotations, totalRotsOnSide)
                                }
                            }
                        }
                    }
                }
            }
        }

        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.disableAlpha()
    }

    private fun drawSceneToGui(framebuffer: Framebuffer, offsetX: Int, offsetY: Int, width: Int, height: Int) {
        val mc = Minecraft.getMinecraft()

        val tess = Tessellator.getInstance()
        val bufferBuilder = tess.buffer

        mc.entityRenderer.setupOverlayRendering()

        framebuffer.bindFramebufferTexture()
        mc.framebuffer.bindFramebuffer(true)

        GlStateManager.translate(offsetX.toDouble(), offsetY.toDouble(), 0.0)
        GlStateManager.disableTexture2D()

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        bufferBuilder.pos(0.0, 0.0, 0.0).color(0.05f, 0.05f, 0.05f, 1.0f).endVertex()
        bufferBuilder.pos(0.0, height.toDouble(), 0.0).color(0.05f, 0.05f, 0.05f, 1.0f).endVertex()
        bufferBuilder.pos(width.toDouble(), height.toDouble(), 0.0).color(0.05f, 0.05f, 0.05f, 1.0f).endVertex()
        bufferBuilder.pos(width.toDouble(), 0.0, 0.0).color(0.05f, 0.05f, 0.05f, 1.0f).endVertex()
        tess.draw()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.enableTexture2D()
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder.pos(0.0, 0.0, 0.0).tex(0.0, 1.0).endVertex()
        bufferBuilder.pos(0.0, height.toDouble(), 0.0).tex(0.0, 0.0).endVertex()
        bufferBuilder.pos(width.toDouble(), height.toDouble(), 0.0).tex(1.0, 0.0).endVertex()
        bufferBuilder.pos(width.toDouble(), 0.0, 0.0).tex(1.0, 1.0).endVertex()
        tess.draw()
        GlStateManager.translate(-offsetX.toDouble(), -offsetY.toDouble(), 0.0)
    }

    private fun drawComponentOverlay(x: Int, y: Int, face: EnumFacing, colorMap: MutableMap<AbstractTileEntityCapabilityComponent, Int>, rotations: Vector3f, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val mc = Minecraft.getMinecraft()
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
                    if (it.component is AbstractTileEntityDirectionalCapabilityComponent) {
                        @Suppress("UNCHECKED_CAST")
                        renderSelectionOutline(x, y, face, it, it.component, colorMap, rotations)
                    }
                } else {
                    when (it) {
                        is TCSlotIO -> {
                            val handler = it.itemHandler
                            if (handler is DynamicInventoryCapability) {
                                val comp = handler.componentParent
                                if (comp is TileEntityInventoryComponent) {
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
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.zLevel = 100.0f
        mainTab.components.forEach {
            if (it is TCSlotIO) {
                val slot = it
                mc.renderItem.renderItemAndEffectIntoGUI(mc.player, slot.stack, slot.xPos + parent.guiX, slot.yPos + parent.guiY)
                mc.renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, slot.stack, slot.xPos + parent.guiX, slot.yPos + parent.guiY, null)
            }
        }
        mc.renderItem.zLevel = 0.0f
    }

    private fun renderMouseSelection(rayTrace: RayTraceResult?) {
        GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5f)
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
    }

    private fun renderNeighbors(sharedFBO: MultiTargetFBO, pos: BlockPos, tess: Tessellator, tcw: TemplateClientWorld, framebuffer: Framebuffer) {
        val mc = Minecraft.getMinecraft()
        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        GlStateManager.enableCull()
        RenderHelper.disableStandardItemLighting()
        mc.entityRenderer.disableLightmap()
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()

        //sharedFBO.bindFramebuffer(true)

        shader.start()

        //OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, sharedFBO.framebufferObject)
        //OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.framebufferObject)
        //GL30.glBlitFramebuffer(0, 0, framebuffer.framebufferWidth, framebuffer.framebufferHeight, 0, 0, sharedFBO.width, sharedFBO.height, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST)

        ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID)
        renderBlocks(BlockRenderLayer.SOLID, pos, tess, tcw)


        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

        /*
            we need to render cutout blocks and tile entitys with depth so they clip properly.
            Because of that we need to use a second framebuffer that only stores the current depth information
            but also renders to the original texture
             */
        for (face in EnumFacing.values()) {
            //clone the depth information so the machine block does look solid
            //GL30.glBlitFramebuffer(0, 0, framebuffer.framebufferWidth, framebuffer.framebufferHeight, 0, 0, sharedFBO.width, sharedFBO.height, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST)

            val offset = pos.offset(face)
            val state = mc.world.getBlockState(offset)
            var render = false
            if (state.block.canRenderInLayer(state, BlockRenderLayer.CUTOUT_MIPPED)) {
                ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT_MIPPED)
                render = true
            }
            if (state.block.canRenderInLayer(state, BlockRenderLayer.CUTOUT)) {
                ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT)
                render = true
            }
            if (render) {
                alphaClip.uploadUniform(true)
                screenSize.uploadUniform(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight)
                shader.updateUniforms()
                tess.buffer.begin(7, DefaultVertexFormats.BLOCK)
                tess.buffer.setTranslation((-pos.x).toDouble(), (-pos.y).toDouble(), (-pos.z).toDouble())
                mc.blockRendererDispatcher.renderBlock(state, offset, tcw, tess.buffer)
                tess.buffer.setTranslation(0.0, 0.0, 0.0)
                tess.draw()
            }

            GlStateManager.pushMatrix()
            ForgeHooksClient.setRenderPass(0)
            var tile = tcw.getTileEntity(pos.offset(face))
            var tileOffset = BlockPos.ORIGIN.offset(face)

            if (tile is TileEntityChest) {
                tile = tile.adjacentChestXNeg?.apply { tileOffset = tileOffset.offset(EnumFacing.WEST) }
                        ?: tile.adjacentChestZNeg?.apply { tileOffset = tileOffset.offset(EnumFacing.NORTH) }
                                ?: tile
            }

            if (tile != null) {
                renderTileEntity(tile, tileOffset)
            }
            Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GlStateManager.popMatrix()
        }

        alphaClip.uploadUniform(false)
        screenSize.uploadUniform(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight)
        shader.updateUniforms()

        GlStateManager.depthMask(false)
        GlStateManager.enableDepth()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.shadeModel(GL11.GL_SMOOTH)

        ForgeHooksClient.setRenderLayer(BlockRenderLayer.TRANSLUCENT)
        renderBlocks(BlockRenderLayer.TRANSLUCENT, pos, tess, tcw)

        ForgeHooksClient.setRenderPass(1)
        renderSurroundingTiles(pos, machine.world)
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        shader.stop()

        OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0)

        framebuffer.bindFramebuffer(true)
    }

    /**
     * Helper function to render the selection outline onto the selected side within the side config
     */
    private fun renderSelectionOutline(x: Int,
                                       y: Int,
                                       facing: EnumFacing,
                                       renderComponent: ITCComponent,
                                       component: AbstractTileEntityDirectionalCapabilityComponent,
                                       colorMap: MutableMap<AbstractTileEntityCapabilityComponent, Int>,
                                       rotation: Vector3f
    ) {
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

    /**
     * A helper function that renders the side-config overlay onto a block
     *
     * @param facing the facing of the block where the overlay is applied
     * @param component the sided component of the machine that is linked to the overlay
     * @param colorMap a mapping of colors for different components
     * @param bb the bounding box used for the 3D overlay renderer
     * @param rotation the rotation of the block
     * @param totalRotsOnSide
     */
    private fun renderBlockOverlay(
            facing: EnumFacing,
            component: AbstractTileEntityDirectionalCapabilityComponent,
            colorMap: MutableMap<AbstractTileEntityCapabilityComponent, Int>,
            bb: AxisAlignedBB,
            rotation: Vector3f,
            totalRotsOnSide: Vector2f
    ): Vector3f {
        val color = colorMap.getOrPut(component) {
            val color = Color.getHSBColor(rotation.x / 360f, 1f, 1f).rgb
            rotation.x += 30
            color
        }


        if (component.facing.contains(facing)) {
            var amount = 4

            val image: String

            var rot = if (component.getDirection() == AbstractTileEntityDirectionalCapabilityComponent.Direction.OUTPUT) {
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

    /**
     * A helper function render tile entities into the gui.
     *
     * @param pos the position of the tile entity
     * @param tcw the world that provides the tile entity
     */
    private fun renderSurroundingTiles(pos: BlockPos, tcw: World) {
        for (face in EnumFacing.values()) {
            var tile = tcw.getTileEntity(pos.offset(face))
            var offset = BlockPos.ORIGIN.offset(face)

            if (tile is TileEntityChest) {
                tile = tile.adjacentChestXNeg?.apply { offset = offset.offset(EnumFacing.WEST) }
                        ?: tile.adjacentChestZNeg?.apply { offset = offset.offset(EnumFacing.NORTH) } ?: tile
            }

            if (tile != null) {
                renderTileEntity(tile, offset)
            }
        }
    }

    private fun renderTileEntity(tile: TileEntity?, pos: BlockPos) {
        val mc = Minecraft.getMinecraft()
        tile ?: return
        val renderer = TileEntityRendererDispatcher.instance.getRenderer<TileEntity>(tile) ?: return

        if (tile.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) {
            //push and pop attribute is slow but some modders dont revert to the right state
            GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT or GL11.GL_COLOR_BUFFER_BIT)
            renderer.render(tile, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), mc.renderPartialTicks, -1, 1f)
            GlStateManager.popAttrib()
            RenderHelper.disableStandardItemLighting()
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

    /**
     * Helper function to render a block into the gui.
     *
     * @param layer the render layer that shall be used
     * @param pos the position of the block to render
     * @param tess the tessellator instance
     * @param tcw the world that provides the block
     */
    private fun renderBlocks(layer: BlockRenderLayer, pos: BlockPos, tess: Tessellator, tcw: World) {
        val mc = Minecraft.getMinecraft()
        var begun = false
        for (face in EnumFacing.values()) {
            val offset = pos.offset(face)
            val state = mc.world.getBlockState(offset)
            if (!state.block.canRenderInLayer(state, layer)) continue

            if (!begun) {
                tess.buffer.begin(7, DefaultVertexFormats.BLOCK)
                begun = true
            }

            tess.buffer.setTranslation((-pos.x).toDouble(), (-pos.y).toDouble(), (-pos.z).toDouble())
            mc.blockRendererDispatcher.renderBlock(state, offset, tcw, tess.buffer)
            tess.buffer.setTranslation(0.0, 0.0, 0.0)
        }
        if (begun)
            tess.draw()
    }
}