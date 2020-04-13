package net.cydhra.technocracy.foundation.content.items.upgrades

import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_GENERIC
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
    }

    override val upgradeParameter = UPGRADE_GENERIC
    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return upgrades.getInstalledUpgrades()
                .filterIsInstance<EnergyUpgrade>()
                .isEmpty()
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.removeComponent(ENERGY_COMPONENT_NAME)
    }

    override fun onUpgradeLoad(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        val energyUpgrade = ItemEnergyComponent(capacity)

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