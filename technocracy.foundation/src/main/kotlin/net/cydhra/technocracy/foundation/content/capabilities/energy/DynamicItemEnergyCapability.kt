package net.cydhra.technocracy.foundation.content.capabilities.energy

import net.cydhra.technocracy.foundation.content.capabilities.IItemCapability
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.minecraft.item.ItemStack


class DynamicItemEnergyCapability(currentEnergy: Int = 0, capacity: Int,
                                  extractionLimit: Int, receivingLimit: Int = -1) : DynamicEnergyCapability(currentEnergy, capacity, extractionLimit, receivingLimit), IItemCapability {
    override fun getContainer(): ItemStack {
        return (componentParent as AbstractItemComponent).wrapper.stack
    }
}