package net.cydhra.technocracy.astronautics.content.items

import net.cydhra.technocracy.astronautics.content.blocks.ScaffoldBlock
import net.cydhra.technocracy.astronautics.content.blocks.wetConcreteBlock
import net.cydhra.technocracy.astronautics.content.blocks.wetReinforcedConcreteBlock
import net.cydhra.technocracy.astronautics.model.items.color.ConcreteSprayerColor
import net.cydhra.technocracy.foundation.content.blocks.color.DyeBlockColor
import net.cydhra.technocracy.foundation.content.items.BaseItem
import net.cydhra.technocracy.foundation.content.items.emptyCanItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.World


class ConcreteSprayerItem : BaseItem("concrete_sprayer", itemColor = ConcreteSprayerColor) {

    init {
        maxDamage = 100
        maxStackSize = 1
    }

    override fun isBookEnchantable(stack: ItemStack, book: ItemStack): Boolean {
        return false
    }

    override fun isDamaged(stack: ItemStack): Boolean {
        return getConcreteType(stack) != null
    }

    override fun getDamage(stack: ItemStack): Int {
        super.getDamage(stack)
        return maxDamage - getConcreteAmount(stack)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean {
        if (entityLiving.isSneaking && entityLiving is EntityPlayer) {

            val type = getConcreteType(stack)
            val amount = getConcreteAmount(stack)

            if (type != null) {
                var can = ItemStack(concreteCanItem)
                if (amount > 0) {
                    can.tagCompound = stack.tagCompound
                } else {
                    can = ItemStack(emptyCanItem)
                }

                stack.tagCompound = null

                entityLiving.inventory.addItemStackToInventory(can)
            }
        }

        return false
    }

    fun findAmmo(player: EntityPlayer): ItemStack {
        when {
            this.isCan(player.getHeldItem(EnumHand.OFF_HAND)) -> return player.getHeldItem(EnumHand.OFF_HAND)
            this.isCan(player.getHeldItem(EnumHand.MAIN_HAND)) -> return player.getHeldItem(EnumHand.MAIN_HAND)
            else -> {
                for (i in 0 until player.inventory.sizeInventory) {
                    val itemstack = player.inventory.getStackInSlot(i)

                    if (this.isCan(itemstack)) {
                        return itemstack
                    }
                }

                return ItemStack.EMPTY
            }
        }
    }

    fun isCan(stack: ItemStack): Boolean {
        return stack.item is ConcreteCanItem && getConcreteAmount(stack) != -1
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val color = getConcreteType(stack) ?: return I18n.translateToLocal("${unlocalizedName}_empty.name")
        return super.getItemStackDisplayName(stack) + " - " + I18n.translateToLocal("color." + color.unlocalizedName + ".name")
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab))
            for (color in EnumDyeColor.values()) {
                val stack = ItemStack(this, 1, 0)
                val compound = NBTTagCompound()
                compound.setInteger("color", color.metadata)
                compound.setInteger("concrete", 100)
                stack.tagCompound = compound
                items.add(stack)
            }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val scaffoldOnly = worldIn.getBlockState(pos).block is ScaffoldBlock
        val stack = player.getHeldItem(hand)
        val color = getConcreteType(stack)
        val maxBlocks = getConcreteAmount(stack)

        if (color == null) {
            val ammo = findAmmo(player)
            if (!ammo.isEmpty) {
                stack.tagCompound = ammo.tagCompound

                ammo.shrink(1)

                if (ammo.isEmpty) {
                    player.inventory.deleteStack(ammo)
                }
            }

            return EnumActionResult.FAIL
        }

        if (maxBlocks <= 0) {
            stack.tagCompound = null
            val can = ItemStack(emptyCanItem)
            player.inventory.addItemStackToInventory(can)
            return EnumActionResult.FAIL
        }

        var placed = 0

        if (maxBlocks > 0) {
            if (scaffoldOnly) {
                var maxX = 2.0
                var maxY = 2.0
                var maxZ = 2.0

                if (scaffoldOnly) {
                    if (facing.axis == EnumFacing.Axis.X) {
                        maxX = 0.0
                    }
                    if (facing.axis == EnumFacing.Axis.Y) {
                        maxY = 0.0
                    }
                    if (facing.axis == EnumFacing.Axis.Z) {
                        maxZ = 0.0
                    }
                }

                if (player.isSneaking) {
                    maxX = 0.0
                    maxY = 0.0
                    maxZ = 0.0
                }

                for (x in -maxX.toInt()..maxX.toInt()) {
                    for (z in -maxZ.toInt()..maxZ.toInt()) {
                        for (y in -maxY.toInt()..maxY.toInt()) {
                            if (placed < maxBlocks) {
                                placed++

                                val newPos = pos.add(x, y, z)
                                val block = worldIn.getBlockState(newPos).block

                                if (block is ScaffoldBlock)
                                    worldIn.setBlockState(newPos, wetReinforcedConcreteBlock.defaultState.withProperty(
                                        DyeBlockColor.COLOR, color))
                            }
                        }
                    }
                }
            } else {
                val width = 3
                val height = 3

                val xCenter = width / 2
                val zCenter = width / 2
                val yCenter = height / 2


                for (x in -xCenter..xCenter) {
                    for (z in -xCenter..xCenter) {
                        for (y in -yCenter..yCenter) {

                            val xDist = (x) / (width / 2.0)
                            val zDist = (z) / (width / 2.0)
                            val yDist = (y) / (height / 2.0)
                            val distXYZInner = xDist * xDist + yDist * yDist + zDist * zDist

                            val newPos = BlockPos(pos.x + x, pos.y + y, pos.z + z).offset(facing)
                            val block = worldIn.getBlockState(newPos).block

                            if (block.isReplaceable(worldIn, newPos)) {
                                if (distXYZInner < 1.0) {
                                    if (placed < maxBlocks) {
                                        placed++
                                        worldIn.setBlockState(newPos, wetConcreteBlock.defaultState.withProperty(
                                            DyeBlockColor.COLOR, color))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!player.isCreative)
            stack.tagCompound!!.setInteger("concrete", maxBlocks - placed)

        //Todo give empty can and set new one

        return EnumActionResult.FAIL
    }

    fun getConcreteType(stack: ItemStack): EnumDyeColor? {
        val nbt = stack.tagCompound ?: return null
        val color = nbt.getInteger("color")
        return EnumDyeColor.byMetadata(color)
    }

    fun getConcreteAmount(stack: ItemStack): Int {
        val nbt = stack.tagCompound ?: return -1
        return nbt.getInteger("concrete")
    }

}