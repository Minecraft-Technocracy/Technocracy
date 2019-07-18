package net.cydhra.technocracy.astronautics.items

import net.cydhra.technocracy.astronautics.items.color.ConcreteSprayerColor
import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.text.translation.I18n


class ConcreteCanItem : BaseItem("concrete_can", itemColor = ConcreteSprayerColor) {
    init {
        maxDamage = 100
        maxStackSize = 1
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val color = getConcreteType(stack) ?: return I18n.translateToLocal("${unlocalizedName}_empty.name")
        return super.getItemStackDisplayName(stack) + " - " + I18n.translateToLocal("color." + color.unlocalizedName + ".name")
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        super.getSubItems(tab, items)

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

    override fun isDamageable(): Boolean {
        return true
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

    fun getConcreteAmount(stack: ItemStack): Int {
        val nbt = stack.tagCompound ?: return 0
        return nbt.getInteger("concrete")
    }

    fun getConcreteType(stack: ItemStack): EnumDyeColor? {
        val nbt = stack.tagCompound ?: return null
        val color = nbt.getInteger("color")
        return EnumDyeColor.byMetadata(color)
    }
}