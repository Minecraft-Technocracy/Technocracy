package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.label.WrappingLabel
import net.cydhra.technocracy.foundation.content.items.wrenchItem
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import org.lwjgl.util.glu.Project

class SideConfigTab(parent: TCGui, val machine: MachineTileEntity) : TCTab("SideConfig", parent, icon = TCIcon(wrenchItem)) {

    companion object {
        const val PADDING_LEFT = 8
        const val PADDING_TOP = 20
        const val PADDING_RIGHT = PADDING_LEFT
        const val SLOT_WIDTH_PLUS_PADDING = 18
        const val UPGRADE_SLOTS_PER_ROW = 3
        const val INFO_LABEL_OFFSET = PADDING_LEFT + UPGRADE_SLOTS_PER_ROW * SLOT_WIDTH_PLUS_PADDING + PADDING_LEFT
    }

    private val lableWidth = (parent.guiWidth - PADDING_RIGHT - INFO_LABEL_OFFSET) / 2
    private val infoTitleLabel = WrappingLabel(
            posX = INFO_LABEL_OFFSET,
            posY = PADDING_TOP,
            maxWidth = lableWidth,
            scaling = 0.7,
            text = ""
    )


    override fun init() {

        components.add(infoTitleLabel)
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

        GlStateManager.pushMatrix()
        val mc = Minecraft.getMinecraft()
        GlStateManager.matrixMode(5889)
        GlStateManager.loadIdentity()

        val farPlaneDistance = 100f
        Project.gluPerspective(110f, mc.displayWidth.toFloat() / mc.displayHeight.toFloat(), 0.005f, farPlaneDistance)
        GlStateManager.matrixMode(5888)
        GlStateManager.loadIdentity()

        val state = machine.getBlockState()

        val tess = Tessellator.getInstance()
        val bufferBuilder = tess.buffer

        val pos = machine.pos

        bufferBuilder.begin(7, DefaultVertexFormats.BLOCK)


        val model = mc.blockRendererDispatcher.getModelForState(state)
        mc.blockRendererDispatcher.blockModelRenderer.renderModel(mc.world, model, state, BlockPos(0,0,0), bufferBuilder, true, MathHelper.getPositionRandom(pos))
        tess.draw()

        GlStateManager.popMatrix()

        //super.draw(x, y, mouseX, mouseY, partialTicks)
    }
}