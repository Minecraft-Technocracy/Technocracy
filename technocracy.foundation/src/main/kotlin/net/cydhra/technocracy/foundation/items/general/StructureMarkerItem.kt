package net.cydhra.technocracy.foundation.items.general

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color


class StructureMarkerItem : BaseItem("structure_marker") {

    companion object {
        var firstPos: BlockPos? = null
        var secondPos: BlockPos? = null

        var controller: BlockPos? = null

        var wildcard = mutableListOf<BlockPos>()
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add("Left click to select first position")
        tooltip.add("Right click to select second position")
        tooltip.add("Sneak + Left click add wildcard block")
        tooltip.add("Sneak + Right click set controller block")
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        if (player.isSneaking) {
            controller = pos
        } else {
            secondPos = pos
        }

        return EnumActionResult.FAIL
    }

    override fun canDestroyBlockInCreative(world: World, pos: BlockPos, stack: ItemStack, player: EntityPlayer): Boolean {
        return false
    }

    override fun onBlockStartBreak(itemstack: ItemStack, pos: BlockPos, player: EntityPlayer): Boolean {

        if (player.isSneaking) {
            if (wildcard.contains(pos)) {
                wildcard.remove(pos)
            } else {
                wildcard.add(pos)
            }
        } else {
            firstPos = pos
        }

        return true
    }

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {
        if (firstPos != null && secondPos != null) {

            val mc = Minecraft.getMinecraft()
            GlStateManager.disableDepth()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()

            if (controller != null) {
                val col = Color.getHSBColor(((mc.player.ticksExisted * 2) % 360).toFloat(), 1.0f, 1.0f)
                RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(controller!!, controller!!.add(1.0, 1.0, 1.0)).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), col.red.toFloat(), col.green.toFloat(), col.blue.toFloat(), 0.8f)
            }

            for (pos in wildcard) {
                RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(pos, pos.add(1.0, 1.0, 1.0)).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), 100 / 255f, 65 / 255f, 165 / 255f, 0.8f)
            }


            RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(0.0, 0.0, 0.0, secondPos!!.x.toDouble() - firstPos!!.x, secondPos!!.y.toDouble() - firstPos!!.y, secondPos!!.z.toDouble() - firstPos!!.z).offset(firstPos!!).expand(1.0, 1.0, 1.0).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), 1f, 0.2f, 0.2f, 0.8f)
            GlStateManager.enableDepth()
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
        }
    }

}