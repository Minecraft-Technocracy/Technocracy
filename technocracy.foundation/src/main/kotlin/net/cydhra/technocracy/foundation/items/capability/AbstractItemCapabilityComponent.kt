package net.cydhra.technocracy.foundation.items.capability

import net.cydhra.technocracy.foundation.tileentity.components.AbstractCapabilityComponent
import net.minecraft.item.ItemStack


abstract class AbstractItemCapabilityComponent(val item: ItemStack) : AbstractCapabilityComponent() {

    lateinit var wrapper: ItemCapabilityWrapper

    override fun markDirty(needsClientRerender: Boolean) {
        wrapper.updateItemStack()
    }
}