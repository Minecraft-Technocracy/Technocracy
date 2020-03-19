package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.button.DefaultButton
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.EnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.CoolantMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.FluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.heatmeter.HeatMeter
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotPlayer
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.items.wrenchItem
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
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
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.MinecraftForgeClient
import org.apache.commons.lang3.text.WordUtils
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project
import java.awt.Color


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

        hideBlocks = DefaultButton(10, offsetY + height - 15 - 2, 15, 15, "H") { player, tile, button ->
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

    var checkMouse = false

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            checkMouse = true

            mainTab.components.forEach {
                if (it !is TCSlotPlayer) {
                    var face = lastSideHit ?: currentLockedSide
                    if (face.axis.isHorizontal)
                        face = face.rotateY().rotateY()

                    when (it) {
                        is EnergyMeter -> {
                            if (it.isMouseOnComponent(mouseX - x, mouseY - y)) {
                                if (!it.component.facing.remove(face)) it.component.facing.add(face)
                            }
                        }
                        is CoolantMeter -> {

                            if (it.meterIn.isMouseOnComponent(mouseX - x - it.posX, mouseY - y - it.posY)) {
                                if (!it.coolantIn.facing.remove(face)) it.coolantIn.facing.add(face)
                            }
                            if (it.meterOut.isMouseOnComponent(mouseX - x - it.posX, mouseY - y - it.posY)) {
                                if (!it.coolantOut.facing.remove(face)) it.coolantOut.facing.add(face)
                            }
                        }
                        is FluidMeter -> {
                            if (it.isMouseOnComponent(mouseX - x, mouseY - y)) {
                                if (!it.component.facing.remove(face)) it.component.facing.add(face)
                            }
                        }
                        is TCSlotIO -> {
                            val handler = it.itemHandler
                            if (handler is DynamicInventoryCapability) {
                                val comp = handler.componentParent
                                if (comp is InventoryTileEntityComponent) {
                                    if (it.isMouseOnComponent(mouseX - x, mouseY - y)) {
                                        if (!comp.facing.remove(face)) comp.facing.add(face)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }

    override fun update() {
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


        GL11.glTranslated(0.0, 0.0, -3.0)
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
            var bb = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
            bb = bb.contract(opposite.frontOffsetX.toDouble(), opposite.frontOffsetY.toDouble(), opposite.frontOffsetZ.toDouble())
            Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("technocracy.foundation", "textures/gui/sideconfig/selection.png"))
            GlStateManager.color(0f, 0.2f, 0.4f)
            OpenGLBoundingBox.drawTexturedBoundingBox(bb)
        }
        GlStateManager.enableDepth()


        if (!hideNeighbors) {
            Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            GlStateManager.enableCull()
            RenderHelper.disableStandardItemLighting()
            mc.entityRenderer.disableLightmap()
            GlStateManager.disableLighting();
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)
            shader.start()

            GlStateManager.disableAlpha()
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID)
            renderBlocks(BlockRenderLayer.SOLID, pos, tess, tcw)

            ForgeHooksClient.setRenderPass(1)
            renderTileEntitys(pos, machine.world)
            Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            //GlStateManager.alphaFunc(516, 0.5f)
            //GlStateManager.enableAlpha()

            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT_MIPPED)
            renderBlocks(BlockRenderLayer.CUTOUT_MIPPED, pos, tess, tcw)
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT)
            renderBlocks(BlockRenderLayer.CUTOUT, pos, tess, tcw)

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)
            //GlStateManager.alphaFunc(516, 0.1f)
            GlStateManager.shadeModel(7425)

            ForgeHooksClient.setRenderLayer(BlockRenderLayer.TRANSLUCENT)
            renderBlocks(BlockRenderLayer.TRANSLUCENT, pos, tess, tcw)

            ForgeHooksClient.setRenderPass(0)
            renderTileEntitys(pos, machine.world)

            ForgeHooksClient.setRenderPass(-1)

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

        this.components.forEach {
            it.draw(x, y, mouseX, mouseY, partialTicks)
        }

        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(mainTab.name, 8f + x, 8f + y, 4210752, false)

        GlStateManager.enableDepth()

        mainTab.components.forEach {
            if (it !is TCSlotPlayer) {

                var face = lastSideHit ?: currentLockedSide
                if (face.axis.isHorizontal)
                    face = face.rotateY().rotateY()

                var dontRender = false

                when (it) {
                    is EnergyMeter -> {
                        if (it.component.facing.contains(face)) {
                            Gui.drawRect(x + it.posX - 1, y + it.posY - 1, x + it.posX + it.width + 1, y + it.posY + it.height + 1, Color(255, 0, 0).rgb)
                        }
                    }
                    is CoolantMeter -> {
                        dontRender = true

                        it.drawBackground(x, y)

                        if (it.coolantIn.facing.contains(face)) {
                            val pX = x + it.posX + it.meterIn.posX
                            val pY = y + it.posY + it.meterIn.posY
                            Gui.drawRect(pX - 1, pY - 1, pX + it.meterIn.width + 1, pY + it.meterIn.height + 1, Color(255, 0, 0).rgb)
                        }
                        if (it.coolantOut.facing.contains(face)) {
                            Gui.drawRect(x + it.posX + it.meterOut.posX - 1, y + it.posY + it.meterOut.posY - 1, x + it.posX + it.meterOut.width + it.meterOut.posX + 1, y + it.posY + it.meterOut.height + it.meterOut.posY + 1, Color(255, 0, 0).rgb)
                        }

                        it.draw(x, y, mouseX, mouseY, -1f)
                    }
                    is FluidMeter -> {
                        if (it.component.facing.contains(face)) {
                            Gui.drawRect(x + it.posX - 1, y + it.posY - 1, x + it.posX + it.width + 1, y + it.posY + it.height + 1, Color(255, 0, 0).rgb)
                        }
                    }
                    is TCSlotIO -> {
                        val handler = it.itemHandler
                        if (handler is DynamicInventoryCapability) {
                            val comp = handler.componentParent
                            if (comp is InventoryTileEntityComponent) {
                                if (comp.facing.contains(face)) {
                                    Gui.drawRect(x + it.xPos - 2, y + it.yPos - 2, x + it.xPos + it.width, y + it.yPos + it.height, Color(255, 0, 0).rgb)
                                }
                            }
                        }
                    }
                }

                if (!dontRender)
                    it.draw(x, y, mouseX, mouseY, partialTicks)
            }
        }
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

    fun renderTileEntitys(pos: BlockPos, tcw: World) {
        val mc = Minecraft.getMinecraft()

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
                    TileEntityRendererDispatcher.instance.render(tile, face.frontOffsetX * 1.0 + offX, face.frontOffsetY * 1.0, face.frontOffsetZ * 1.0 + offZ, mc.renderPartialTicks)
                }
            }
        }
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