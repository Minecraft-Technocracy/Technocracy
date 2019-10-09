package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemPolishedMirror : BaseItem("polished_mirror") {

    @SideOnly(Side.CLIENT)
    override fun hasEffect(stack: ItemStack): Boolean {
        return true
    }
}