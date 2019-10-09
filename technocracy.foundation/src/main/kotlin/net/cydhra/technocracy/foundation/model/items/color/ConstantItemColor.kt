package net.cydhra.technocracy.foundation.model.items.color

import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.ItemStack

/**
 * An entity that returns a constant value as item color for immutably tinted items
 */
class ConstantItemColor(private val constantItemColor: Int) : IItemColor {
    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        return this.constantItemColor
    }
}