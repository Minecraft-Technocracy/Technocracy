package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.api.wrench.IWrench
import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.util.IItemScrollEvent
import net.minecraft.client.Minecraft
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


class WrenchItem : BaseItem("wrench"), IWrench, IItemScrollEvent {

    init {
        for (type in PipeType.values()) {
            EnumHelper.addEnum(WrenchMode::class.java, "PIPE_${type.name}", arrayOf(String::class.java, PipeType::class.java), "pipe.${type.unlocalizedName}", type)
        }
        maxStackSize = 1
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END)
            return
        val player = Minecraft.getMinecraft().player ?: return
        val stack = player.heldItemMainhand
        if (!stack.isEmpty && stack.item == this) {
            val mode = getWrenchMode(stack)
            if (mode == WrenchMode.DEFAULT) {
                for (pt in PipeType.values()) {
                    TextureAtlasManager.getTextureForConnectionType(pt).setAnimationTime(0)
                }
            } else {
                for (pt in PipeType.values()) {
                    if (mode.allowedPipe == pt) {
                        TextureAtlasManager.getTextureForConnectionType(pt).setAnimationTime(0)
                    } else {
                        TextureAtlasManager.getTextureForConnectionType(pt).setAnimationTime(1)
                    }
                }
            }
        } else {
            for (pt in PipeType.values()) {
                TextureAtlasManager.getTextureForConnectionType(pt).setAnimationTime(0)
            }
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