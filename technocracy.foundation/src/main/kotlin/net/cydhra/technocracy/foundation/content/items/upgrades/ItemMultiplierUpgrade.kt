package net.cydhra.technocracy.foundation.content.items.upgrades

import net.cydhra.technocracy.foundation.api.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.minecraft.util.text.*
import java.util.*


abstract class ItemMultiplierUpgrade(
        val multiplier: Double,
        override val upgradeParameter: UpgradeParameter
) : ItemUpgrade() {

    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return upgradable.supportsParameter(this.upgradeParameter)
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.upgradeParameter(this.upgradeParameter, this.multiplier)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.upgradeParameter(this.upgradeParameter, -this.multiplier)
    }

    override fun onUpgradeLoad(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(TextComponentTranslation("tooltips.upgrades.parameter.$upgradeParameter")
                .setStyle(Style()
                        .setColor(TextFormatting.AQUA))
                .appendSibling(TextComponentString(": ")
                        .setStyle(Style()
                                .setColor(TextFormatting.AQUA)))
                .appendSibling(
                        TextComponentString("${if (this.multiplier > 0) "+" else ""}${(multiplier * 100).toInt()}%")
                                .setStyle(Style()
                                        .setColor(TextFormatting.WHITE))))
    }
}