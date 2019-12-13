package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * A machine component that handles all machine upgrades.
 *
 * @param supportedUpgradeTypes a set of supported upgrade types. If a player tries to install an upgrade into the
 * machine, all of its upgrade types must be supported for installation to work.
 * @param numberOfUpgradeSlots how many upgrade slots the machine has.
 */
class MachineUpgradesComponent(val numberOfUpgradeSlots: Int,
                               val supportedUpgradeTypes: Set<MachineUpgradeParameter>,
                               val supportedUpgradeClasses: Set<MachineUpgradeClass>) : AbstractComponent(), TEInventoryProvider {

    override val type: ComponentType = ComponentType.OTHER

    /**
     * The inventory where to place upgrade items. Is public because GUI must be able to access it for modification
     */
    val inventory: DynamicInventoryCapability = DynamicInventoryCapability(numberOfUpgradeSlots, this)

    init {
        this.inventory.componentParent = this
    }

    override fun serializeNBT(): NBTTagCompound {
        return inventory.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt)
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        assert(inventory == this.inventory)
        val item = stack.item

        // only accept upgrade items
        if (item !is UpgradeItem)
            return false

        // check whether this component accepts upgrades of the given item's upgrade class
        if (!this.supportedUpgradeClasses.contains(item.upgradeClass))
            return false

        // check whether the upgrade item's parameters are all supported
        if (!item.upgrades.all { upgrade -> this.supportedUpgradeTypes.contains(upgrade.upgradeType) })
            return false

        return true
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack) {

    }
}