package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MultiplierUpgrade
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.*
import kotlin.math.roundToInt

/**
 * A machine component that handles all machine upgrades.
 *
 * @param supportedUpgradeTypes a set of supported upgrade types. If a player tries to install an upgrade into the
 * machine, all of its upgrade types must be supported for installation to work.
 * @param numberOfUpgradeSlots how many upgrade slots the machine has.
 * @param supportedUpgradeClasses the [MachineUpgradeClass]es that are supported by this component's machine.
 * Upgrades must be of one of these classes
 * @param multipliers the multiplier components of the machine that can be upgraded. For each [MultiplierUpgrade]
 * that is supported by this component, a respective [MultiplierComponent] must be added to this set
 */
class MachineUpgradesComponent(val numberOfUpgradeSlots: Int, val supportedUpgradeTypes: Set<MachineUpgradeParameter>,
        val supportedUpgradeClasses: Set<MachineUpgradeClass>, val multipliers: Set<MultiplierComponent>) :
        AbstractComponent(), TEInventoryProvider {

    private val descriptionLines = mutableListOf<Pair<ITextComponent, ITextComponent>>()

    override val type: ComponentType = ComponentType.OTHER

    /**
     * The inventory where to place upgrade items. Is public because GUI must be able to access it for modification
     */
    val inventory: DynamicInventoryCapability = DynamicInventoryCapability(numberOfUpgradeSlots, this)

    /**
     * A list of text lines describing the effects of all installed upgrades
     */
    val description: List<Pair<ITextComponent, ITextComponent>> = descriptionLines

    init {
        this.inventory.componentParent = this
    }

    override fun serializeNBT(): NBTTagCompound {
        return inventory.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt)
        this.updateDescription()
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        assert(inventory == this.inventory)
        val item = stack.item

        // only accept upgrade items
        if (item !is UpgradeItem) return false

        // check whether this component accepts upgrades of the given item's upgrade class
        if (!this.supportedUpgradeClasses.contains(item.upgradeClass)) return false

        // check whether the upgrade item's parameters are all supported
        if (!item.upgrades.all { upgrade -> this.supportedUpgradeTypes.contains(upgrade.upgradeType) }) return false

        return true
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack,
            originalStack: ItemStack) {
        if (stack.item == originalStack.item) return

        if (stack.item == Items.AIR) {
            val upgradeItem = originalStack.item

            if (upgradeItem !is UpgradeItem) {
                throw IllegalStateException("Non-upgrade item was installed in upgrade slot.")
            }

            upgradeItem.upgrades.forEach { upgrade ->
                if (upgrade is MultiplierUpgrade) {
                    val componentToUpgrade = this.multipliers.single { it.upgradeParameter == upgrade.upgradeType }
                    componentToUpgrade.multiplier -= upgrade.multiplier
                    println(componentToUpgrade.upgradeParameter + ": " + componentToUpgrade.multiplier)
                } else {
                    // TODO complex upgrade uninstalling
                }
            }
        } else {
            val upgradeItem = stack.item

            if (upgradeItem !is UpgradeItem) {
                throw IllegalStateException("Non-upgrade item installed in upgrade slot.")
            }

            upgradeItem.upgrades.forEach { upgrade ->
                if (upgrade is MultiplierUpgrade) {
                    val componentToUpgrade = this.multipliers.single { it.upgradeParameter == upgrade.upgradeType }
                    componentToUpgrade.multiplier += upgrade.multiplier
                    println(componentToUpgrade.upgradeParameter + ": " + componentToUpgrade.multiplier)
                } else {
                    // TODO complex upgrade installation
                }
            }
        }

        this.updateDescription()
    }

    fun updateDescription() {
        this.descriptionLines.clear()
        this.descriptionLines.add(TextComponentTranslation("tooltips.upgrades.title.description")
                .appendSibling(TextComponentString(":")) to TextComponentString(
                "${this.inventory.stacks.filter { !it.isEmpty }.count()}/${this.numberOfUpgradeSlots}")
                .setStyle(Style().setColor(TextFormatting.GOLD)))

        this.multipliers.forEach { multiplier ->
            this.descriptionLines.add(TextComponentTranslation("tooltips.upgrades.parameter.${multiplier.upgradeParameter}")
                    .appendSibling(TextComponentString(":")) to TextComponentString(
                    "${(multiplier.getCappedMultiplier() * 100).roundToInt()}%")
                    .setStyle(Style().setColor(TextFormatting.GOLD)))
        }
    }
}