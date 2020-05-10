package net.cydhra.technocracy.foundation.content.items.upgrades

import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicItemEnergyCapability
import net.cydhra.technocracy.foundation.content.items.components.ItemBatteryAddonComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import java.util.*


/**
 * An upgrade that enables the item to accept energy.
 */
class EnergyUpgrade(val capacity: Int) : ItemUpgrade() {

    companion object {
        const val ENERGY_COMPONENT_NAME = "energy_upgrade"
        const val BATTERY_ADDON: UpgradeParameter = "battery_addon"
    }

    override val upgradeParameter = BATTERY_ADDON
    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return upgrades
                .getInstalledUpgrades()
                .filterIsInstance<EnergyUpgrade>()
                .isEmpty() &&
                upgradable.getComponents()
                        .map { it.second }
                        .filterIsInstance<ItemBatteryAddonComponent>().isNotEmpty()
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.removeComponent(ENERGY_COMPONENT_NAME)
    }

    override fun onUpgradeLoad(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        val addon = upgradable.getComponents()
                .map { it.second }
                .filterIsInstance<ItemBatteryAddonComponent>()
                .first()

        val energyUpgrade = ItemEnergyComponent(DynamicItemEnergyCapability(0, capacity, (capacity * addon.extractionScaler).toInt(), (capacity * addon.insertScaler).toInt()))
        energyUpgrade.needsClientSyncing = true
        energyUpgrade.energyStorage.needsClientSyncing = true

        // since loads are not only triggered when loading the chunk, but also upon packages from server, check
        // whether the component is already present
        if (upgradable.getComponents().none { it.first == ENERGY_COMPONENT_NAME }) {
            upgradable.registerComponent(energyUpgrade, ENERGY_COMPONENT_NAME)
        }
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(
                TextComponentTranslation("tooltips.upgrades.hint.energy $capacity")
                        .setStyle(Style().setColor(TextFormatting.GREEN))
        )
    }
}