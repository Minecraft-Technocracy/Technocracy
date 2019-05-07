package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World


class ItemHotGlueGun : BaseItem("hotgluegun") {
    init {
        maxStackSize = 1
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        println("pew")
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }
}