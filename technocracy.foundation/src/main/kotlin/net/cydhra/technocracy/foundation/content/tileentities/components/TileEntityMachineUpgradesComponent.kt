package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent
import net.cydhra.technocracy.foundation.api.tileentities.TCMachineTileEntity
import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.Upgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A machine component that handles all machine upgrades.
 *
 * @param numberOfUpgradeSlots how many upgrade slots the machine has.
 */
class TileEntityMachineUpgradesComponent(val numberOfUpgradeSlots: Int) : AbstractTileEntityComponent(), TEInventoryProvider<DynamicInventoryCapability> {

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

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        assert(inventory == this.inventory)
        val item = stack.item

        // only accept upgrade items
        if (item !is UpgradeItem<*>) return false

        // check whether the upgrade is a machine-type upgrade
        if (item.upgradeClass != UpgradeClass.MACHINE) return false

        // ask the item whether it can be installed
        if (!item.upgrades.all { upgrade -> (upgrade as MachineUpgrade).canInstallUpgrade(this.tile as TCMachineTileEntity, this) })
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

            upgradeItem.upgrades.forEach { upgrade ->
                (upgrade as MachineUpgrade).onUninstallUpgrade(this.tile as TCMachineTileEntity, this)
            }
        } else {
            val upgradeItem = stack.item

            if (upgradeItem !is UpgradeItem<*>) {
                throw IllegalStateException("Non-upgrade item installed in upgrade slot.")
            }

            upgradeItem.upgrades.forEach { upgrade ->
                (upgrade as MachineUpgrade).onInstallUpgrade(this.tile as TCMachineTileEntity, this)
            }
        }

        this.tile.markDirty()
        this.notifyBlockUpdate()
        this.updateDescription()
    }

    fun getInstalledUpgrades(): List<MachineUpgrade> {
        return this.inventory.stacks
                .asSequence()
                .filter { !it.isEmpty }
                .map { it.item }
                .filterIsInstance<UpgradeItem<*>>()
                .map { it.upgrades.toList() }
                .flatten()
                .filterIsInstance<MachineUpgrade>()
                .toList()
    }

    override fun onLoadAggregate() {
        super.onLoadAggregate()
        this.getInstalledUpgrades().forEach { it.onUpgradeLoad(this.tile as MachineTileEntity, this) }
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

//        this.multipliers.forEach { multiplier ->
//            this.description.add(TextComponentTranslation("tooltips.upgrades.parameter.${multiplier.upgradeParameter}")
//                    .appendSibling(TextComponentString(":")) to TextComponentString(
//                    "${(multiplier.getCappedMultiplier() * 100).roundToInt()}%")
//                    .setStyle(Style().setColor(TextFormatting.DARK_GREEN)))
//        }

        this.description.add(TextComponentString("\n") to TextComponentString("\n"))
    }
}