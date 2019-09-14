package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.api.IWrench
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.cydhra.technocracy.foundation.items.general.IItemScrollEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.Event


class WrenchItem : BaseItem("Wrench"), IWrench, IItemScrollEvent {

    init {
        for (type in PipeType.values()) {
            EnumHelper.addEnum(WrenchMode::class.java, "PIPE_${type.name}", arrayOf(String::class.java, PipeType::class.java), "pipe.${type.unlocalizedName}", type)
        }
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val stack = player.getHeldItem(hand)
        if (!world.isRemote) {
            val block = world.getBlockState(pos).block

            if (getWrenchMode(stack) == WrenchMode.DEFAULT) { //Rotate and disassemble
                if (!player.isSneaking && block.rotateBlock(world, pos, side)) {
                    player.swingArm(hand)
                    return EnumActionResult.FAIL
                } else {
                    val event = PlayerInteractEvent.RightClickBlock(player, hand, pos, side, Vec3d(hitX.toDouble(), hitY.toDouble(), hitZ.toDouble()))
                    if (MinecraftForge.EVENT_BUS.post(event) || event.result == Event.Result.DEFAULT || event.useBlock == Event.Result.DENY || event.useItem == Event.Result.DENY) {
                        return EnumActionResult.PASS
                    }
                }
                return EnumActionResult.SUCCESS
            }
        }
        return EnumActionResult.PASS
    }

    override fun doesSneakBypassUse(stack: ItemStack, world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return true
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return super.getItemStackDisplayName(stack) + " " + I18n.translateToLocal(getWrenchMode(stack).getUnlocalizedName())
    }

    override fun mouseScroll(player: EntityPlayer, itemStack: ItemStack, dir: Int) {
        var currMode = getWrenchMode(itemStack).ordinal

        currMode += dir

        if (currMode < 0)
            currMode = WrenchMode.values().size - 1

        setWrenchMode(itemStack, WrenchMode.values()[currMode % WrenchMode.values().size])
    }

    fun setWrenchMode(stack: ItemStack, mode: WrenchMode) {
        if (!stack.hasTagCompound()) stack.tagCompound = NBTTagCompound()
        stack.tagCompound!!.setInteger("mode", mode.ordinal)
    }

    fun getWrenchMode(stack: ItemStack): WrenchMode {
        val nbt = stack.tagCompound ?: return WrenchMode.DEFAULT
        val mode = nbt.getInteger("mode")

        return WrenchMode.values()[mode]
    }

    enum class WrenchMode(private val displayName: String, val allowedPipe: PipeType? = null) {
        DEFAULT("default");

        fun getUnlocalizedName(): String {
            return "wrenchmode.$displayName.name"
        }
    }
}