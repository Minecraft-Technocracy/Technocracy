package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.util.IItemScrollEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color


class StructureMarkerItem : BaseItem("structure_marker"), IItemScrollEvent {
    override fun mouseScroll(player: EntityPlayer, itemStack: ItemStack, dir: Int) {
        var currMode = getCurrentMode(itemStack)

        if (currMode == -1) {
            itemStack.tagCompound = NBTTagCompound()
        }

        currMode += dir

        if (currMode < 0)
            currMode = StructureSelectModes.values().size - 1

        itemStack.tagCompound!!.setInteger("mode", currMode % StructureSelectModes.values().size)
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val currMode = getCurrentMode(stack)

        if (currMode == -1) {
            return super.getItemStackDisplayName(stack)
        }

        val mode = StructureSelectModes.values()[currMode]

        if (mode == StructureSelectModes.MODULE) {
            return super.getItemStackDisplayName(stack) + " - " + mode.display + " ($currModule)"
        }
        return super.getItemStackDisplayName(stack) + " - " + mode.display
    }

    fun getCurrentMode(itemStack: ItemStack): Int {
        return if (itemStack.tagCompound != null) {
            Math.abs(itemStack.tagCompound!!.getInteger("mode"))
        } else -1
    }

    companion object {
        var firstPos: BlockPos? = null
        var secondPos: BlockPos? = null

        var controller: BlockPos? = null

        var wildcard = mutableListOf<BlockPos>()
        var modules = mutableMapOf<Int, MutableList<BlockPos>>()
        var currModule = 0
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {

        val currMode = getCurrentMode(stack)

        if (currMode == -1) {
            return
        }
        val mode = StructureSelectModes.values()[currMode]

        when (mode) {
            StructureSelectModes.AABBSELECTION -> {
                tooltip.add("Left click to select first pos")
                tooltip.add("Right click to select second pos")
            }
            StructureSelectModes.CONTROLLER -> {
                tooltip.add("Left click to set controller")
                tooltip.add("Right click to remove controller")
            }
            StructureSelectModes.WILDCARD -> {
                tooltip.add("Left click to add wildcard")
                tooltip.add("Sneak Left click to remove wildcard")
            }
            StructureSelectModes.MODULE -> {
                tooltip.add("Left click to add module block")
                tooltip.add("Sneak Left click to remove module block")
                tooltip.add("Right click to increase Counter")
                tooltip.add("Sneak Right click to decrease Counter")
            }
        }


        /* tooltip.add("Left click to select first position")
        tooltip.add("Right click to select second position")
        tooltip.add("Sneak + Left click add wildcard block")
        tooltip.add("Sneak + Right click set controller block")*/
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val currMode = getCurrentMode(player.getHeldItem(hand))

        if (currMode == -1) {
            return EnumActionResult.PASS
        }
        val mode = StructureSelectModes.values()[currMode]

        when (mode) {
            StructureSelectModes.AABBSELECTION -> {
                secondPos = pos
            }
            StructureSelectModes.CONTROLLER -> {
                controller = null
            }
            else -> {
            }
        }

        return EnumActionResult.PASS
    }

    override fun onItemRightClick(worldIn: World, player: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {

        if(worldIn.isRemote)
            return super.onItemRightClick(worldIn, player, handIn)

        val currMode = getCurrentMode(player.heldItemMainhand)

        if (currMode == -1) {
            return super.onItemRightClick(worldIn, player, handIn)
        }
        val mode = StructureSelectModes.values()[currMode]

        when (mode) {
            StructureSelectModes.MODULE -> {
                currModule = if (player.isSneaking) {
                    Math.max(currModule - 1, 0)
                } else {
                    Math.min(currModule + 1, modules.size)
                }
                player.swingArm(handIn)
            }
            else -> {
            }
        }

        return super.onItemRightClick(worldIn, player, handIn)
    }

    override fun canDestroyBlockInCreative(world: World, pos: BlockPos, stack: ItemStack, player: EntityPlayer): Boolean {
        return false
    }

    override fun onBlockStartBreak(stack: ItemStack, pos: BlockPos, player: EntityPlayer): Boolean {

        val currMode = getCurrentMode(stack)

        if (currMode == -1) {
            return true
        }
        val mode = StructureSelectModes.values()[currMode]

        when (mode) {
            StructureSelectModes.AABBSELECTION -> {
                firstPos = pos
            }
            StructureSelectModes.CONTROLLER -> {
                controller = pos
            }
            StructureSelectModes.WILDCARD -> {
                if(player.isSneaking) {
                    wildcard.remove(pos)
                } else {
                    if (!wildcard.contains(pos))
                        wildcard.add(pos)
                }
            }
            StructureSelectModes.MODULE -> {
                if (!(modules[currModule] == null && player.isSneaking)) {
                    val list = modules.getOrPut(currModule) { mutableListOf() }
                    if (player.isSneaking) {
                        list.remove(pos)
                    } else {
                        if (!list.contains(pos))
                            list.add(pos)
                    }
                }
            }
        }

        return true
    }

    @SubscribeEvent
    fun renderOverlay(event: RenderGameOverlayEvent) {
        val stack = Minecraft.getMinecraft().player.heldItemMainhand
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && !stack.isEmpty && stack.item == this) {
            var name: String = stack.displayName
            if (stack.hasDisplayName())
                name = TextFormatting.ITALIC.toString() + name

            name = stack.item.getHighlightTip(stack, name)

            var y = event.resolution.scaledHeight - 59
            if (!Minecraft.getMinecraft().playerController.shouldDrawHUD()) y += 14

            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            val font = stack.item.getFontRenderer(stack)
            if (font != null) {
                val x = (event.resolution.scaledWidth - font.getStringWidth(name)) / 2
                font.drawStringWithShadow(name, x.toFloat(), y.toFloat(), 0xFFFFFF)
            } else {
                val x = (event.resolution.scaledWidth - Minecraft.getMinecraft().fontRenderer.getStringWidth(name)) / 2
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(name, x.toFloat(), y.toFloat(), 0xFFFFFF)
            }
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
        }
    }

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {


        val mc = Minecraft.getMinecraft()
        GlStateManager.disableDepth()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()

        if (controller != null) {
            val col = Color.getHSBColor(((mc.player.ticksExisted * 2) % 360) / 360f, 1.0f, 1.0f)
            RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(controller!!, controller!!.add(1.0, 1.0, 1.0)).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), col.red / 255f, col.green / 255f, col.blue / 255f, 0.8f)
        }

        for (pos in wildcard) {
            RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(pos, pos.add(1.0, 1.0, 1.0)).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), 100 / 255f, 65 / 255f, 165 / 255f, 0.8f)
        }


        for (mod in modules.entries) {
            if (mod.key == currModule)
                for (pos in mod.value)
                    RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(pos, pos.add(1.0, 1.0, 1.0)).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), 0.1f, 0.1f, 1f, 0.8f)
        }

        if (firstPos != null && secondPos != null) {
            RenderGlobal.drawSelectionBoundingBox(AxisAlignedBB(0.0, 0.0, 0.0, secondPos!!.x.toDouble() - firstPos!!.x, secondPos!!.y.toDouble() - firstPos!!.y, secondPos!!.z.toDouble() - firstPos!!.z).offset(firstPos!!).expand(1.0, 1.0, 1.0).offset(-mc.renderManager.viewerPosX, -mc.renderManager.viewerPosY, -mc.renderManager.viewerPosZ), 1f, 0.2f, 0.2f, 0.8f)
        }
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

    }

    enum class StructureSelectModes(val display: String) {
        AABBSELECTION("AABB"), CONTROLLER("Controller"), WILDCARD("Wildcard"), MODULE("Module")
    }
}