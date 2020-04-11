package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.foundation.model.items.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.model.items.api.upgrades.ItemUpgradeClass
import net.cydhra.technocracy.foundation.model.items.capability.AbstractItemCapabilityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.TileEntityMultiplierUpgrade
import net.cydhra.technocracy.foundation.model.upgrades.UpgradeParameter
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.*
import net.minecraftforge.common.capabilities.Capability
import java.util.concurrent.CopyOnWriteArrayList

class ItemUpgradesComponent(val numberOfUpgradeSlots: Int,
                            val supportedUpgradeTypes: Set<UpgradeParameter>,
                            val supportedUpgradeClasses: Set<ItemUpgradeClass>, val multipliers: Set<MultiplierItemComponent>) :
        AbstractItemCapabilityComponent(), TEInventoryProvider {

    /**
     * A set of descriptive lines about installed upgrades
     */
    val description = CopyOnWriteArrayList<Pair<ITextComponent, ITextComponent>>()

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
        this.updateDescription()
    }

    override fun onRegister() {
        TODO("Not yet implemented")
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        assert(inventory == this.inventory)
        val item = stack.item

        // only accept upgrade items
        if (item !is UpgradeItem<*>) return false

        val upgrades = item.upgrades.filterIsInstance<ItemUpgrade>()

        // check whether this component accepts upgrades of the given item's upgrade class
        if (!this.supportedUpgradeClasses.contains(item.upgradeClass)) return false

        // check whether the upgrade item's parameters are all supported
        if (!upgrades.all { upgrade -> this.supportedUpgradeTypes.contains(upgrade.upgradeType) }) return false

        // ask the item whether it can be installed
        if (!upgrades.all { upgrade -> upgrade.canInstallUpgrade(wrapper.stack, this) })
            return false

        return true
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack,
                              originalStack: ItemStack) {
        if (stack.item == originalStack.item) return

        if (stack.item == Items.AIR) {
            val upgradeItem = originalStack.item

            if (upgradeItem !is UpgradeItem<*>) {
                throw IllegalStateException("Non-upgrade item was installed in upgrade slot.")
            }

            val upgrades = upgradeItem.upgrades.filterIsInstance<ItemUpgrade>()

            upgrades.forEach { upgrade ->
                if (upgrade is TileEntityMultiplierUpgrade) {
                    val componentToUpgrade = this.multipliers.single { it.upgradeParameter == upgrade.upgradeType }
                    componentToUpgrade.multiplier -= upgrade.multiplier
                } else {
                    upgrade.onUninstallUpgrade(wrapper.stack, this)
                }
            }
        } else {
            val upgradeItem = stack.item

            if (upgradeItem !is UpgradeItem<*>) {
                throw IllegalStateException("Non-upgrade item installed in upgrade slot.")
            }
            val upgrades = upgradeItem.upgrades.filterIsInstance<ItemUpgrade>()

            upgrades.forEach { upgrade ->
                if (upgrade is TileEntityMultiplierUpgrade) {
                    val componentToUpgrade = this.multipliers.single { it.upgradeParameter == upgrade.upgradeType }
                    componentToUpgrade.multiplier += upgrade.multiplier
                } else {
                    upgrade.onInstallUpgrade(wrapper.stack, this)
                }
            }
        }

        wrapper.updateItemStack()
        this.updateDescription()
    }

    fun getInstalledUpgrades(): List<ItemUpgrade> {
        return this.inventory.stacks
                .asSequence()
                .filter { !it.isEmpty }
                .map { it.item }
                .filterIsInstance<UpgradeItem<*>>()
                .map { it.upgrades.toList() }
                .flatten()
                .filterIsInstance<ItemUpgrade>()
                .toList()
    }

    override fun onLoadAggregate() {
        this.getInstalledUpgrades().forEach { it.onUpgradeLoad(wrapper.stack, this) }
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        TODO("Not yet implemented")
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Update the [description] list with the current upgrade modifiers.
     */
    private fun updateDescription() {
        this.description.clear()

        this.description.add(TextComponentTranslation("tooltips.upgrades.title.description")
                .appendSibling(TextComponentString(":")) to TextComponentString(
                "${this.inventory.stacks.filter { !it.isEmpty }.count()}/${this.numberOfUpgradeSlots}")
                .setStyle(Style().setColor(TextFormatting.DARK_GREEN)))

        val textClasses = this.supportedUpgradeClasses.map { TextComponentTranslation(it.getUnlocalizedName()) }
        val classDescription = textClasses[0]
        for (i in (1 until textClasses.size)) {
            classDescription.appendSibling(TextComponentString(", ")).appendSibling(textClasses[i])
        }
        classDescription.style = Style().setColor(TextFormatting.DARK_GREEN)
        this.description.add(TextComponentTranslation("tooltips.upgrades.title.class").setStyle(Style()
                .setColor(TextFormatting.DARK_PURPLE))
                .appendSibling(TextComponentString(":")) to classDescription)

        this.description.add(TextComponentString("\n") to TextComponentString("\n"))
    }
}