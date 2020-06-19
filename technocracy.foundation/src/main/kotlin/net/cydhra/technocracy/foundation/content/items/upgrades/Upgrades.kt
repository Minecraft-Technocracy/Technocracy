package net.cydhra.technocracy.foundation.content.items.upgrades

import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.api.upgrades.ItemInstallUpgrade
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import java.util.*


/**
 * An upgrade that enables the item to accept energy.
 */
class EnergyUpgrade(val capacity: Int) : ItemInstallUpgrade(INSTALL_ENERGY) {

    companion object {
        const val INSTALL_ENERGY: UpgradeParameter = "energy"
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        super.onInstallUpgrade(upgradable, upgrades)
        val comp = upgradable.getAttachableParameter<ItemEnergyComponent>(this.upgradeParameter)!!
        comp.innerComponent.energyStorage.capacity = capacity
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        super.onUninstallUpgrade(upgradable, upgrades)
        val comp = upgradable.getAttachableParameter<ItemEnergyComponent>(this.upgradeParameter)!!
        comp.innerComponent.energyStorage.capacity = 0
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(
                TextComponentTranslation("tooltips.upgrades.hint.energy $capacity")
                        .setStyle(Style().setColor(TextFormatting.GREEN))
        )
    }
}