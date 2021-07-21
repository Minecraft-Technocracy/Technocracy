package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicItemHolderCapability
import net.cydhra.technocracy.foundation.content.items.UpgradeItem
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.*
import java.util.concurrent.CopyOnWriteArrayList


class ItemUpgradesComponent(val upgradeType: List<UpgradeClass>) : AbstractItemComponent(), TEInventoryProvider<DynamicItemHolderCapability> {

    /**
     * A set of descriptive lines about installed upgrades
     */
    val description = CopyOnWriteArrayList<Pair<ITextComponent, ITextComponent>>()

    override val type: ComponentType = ComponentType.OTHER

    val inv = object : TEInventoryProvider<DynamicInventoryCapability> {
        override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
            assert(inventory == this@ItemUpgradesComponent.dummySlot)
            val item = stack.item

            // only accept upgrade items
            if (item !is UpgradeItem<*>) return false

            // check whether the upgrade is a machine-type upgrade
            if (!upgradeType.contains(item.upgradeClass)) return false

            // ask the item whether it can be installed
            if (!item.upgrades.all { upgrade -> (upgrade as ItemUpgrade).canInstallUpgrade(wrapper, this@ItemUpgradesComponent) })
                return false

            return true
        }

        override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
            inventory.forceStackInSlot(slot, ItemStack.EMPTY)
            if (stack.item != Items.AIR) {
                itemHolder.addStack(stack)
            }
        }
    }

    /**
     * The inventory where to place upgrade items. Is public because GUI must be able to access it for modification
     */
    val dummySlot: DynamicInventoryCapability = DynamicInventoryCapability(1, inv)

    /**
     * The inventory where to place upgrade items. Is public because GUI must be able to access it for modification
     */
    val itemHolder: DynamicItemHolderCapability = DynamicItemHolderCapability(this)


    init {
        this.dummySlot.componentParent = this
        this.itemHolder.componentParent = this
        needsClientSyncing = true
    }

    override fun serializeNBT(): NBTTagCompound {
        return itemHolder.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        itemHolder.deserializeNBT(nbt)
        this.updateDescription()
    }

    override fun isItemValid(inventory: DynamicItemHolderCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun onSlotUpdate(inventory: DynamicItemHolderCapability, slot: Int, stack: ItemStack,
                              originalStack: ItemStack) {
        if (stack.item == originalStack.item) return

        if (stack.item == Items.AIR) {
            val upgradeItem = originalStack.item

            if (upgradeItem !is UpgradeItem<*>) {
                throw IllegalStateException("Non-upgrade item was installed in upgrade slot.")
            }

            upgradeItem.upgrades.forEach { upgrade ->
                (upgrade as ItemUpgrade).onUninstallUpgrade(wrapper, this)
            }
        } else {
            val upgradeItem = stack.item

            if (upgradeItem !is UpgradeItem<*>) {
                throw IllegalStateException("Non-upgrade item installed in upgrade slot.")
            }

            upgradeItem.upgrades.forEach { upgrade ->
                (upgrade as ItemUpgrade).onInstallUpgrade(wrapper, this)
            }
        }

        this.wrapper.updateItemStack()
        this.updateDescription()
    }

    fun getInstalledUpgrades(): List<ItemUpgrade> {
        return this.itemHolder.getStacks()
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
        super.onLoadAggregate()
        this.getInstalledUpgrades().forEach { it.onUpgradeLoad(wrapper, this) }
    }

    /**
     * Update the [description] list with the current upgrade modifiers.
     */
    private fun updateDescription() {
        this.description.clear()

        this.description.add(TextComponentTranslation("tooltips.upgrades.title.description")
                .appendSibling(TextComponentString(":")) to TextComponentString(
                "${this.itemHolder.getStacks().filter { !it.isEmpty }.count()}")
                .setStyle(Style().setColor(TextFormatting.DARK_GREEN)))

//        this.multipliers.forEach { multiplier ->
//            this.description.add(TextComponentTranslation("tooltips.upgrades.parameter.${multiplier.upgradeParameter}")
//                    .appendSibling(TextComponentString(":")) to TextComponentString(
//                    "${(multiplier.getCappedMultiplier() * 100).roundToInt()}%")
//                    .setStyle(Style().setColor(TextFormatting.DARK_GREEN)))
//        }

        this.description.add(TextComponentString("\n") to TextComponentString("\n"))
    }
}