package net.cydhra.technocracy.foundation.model.items.api.upgrades

import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.util.text.*
import java.util.*


abstract class ItemInstallUpgrade(
        override val upgradeParameter: UpgradeParameter
) : ItemUpgrade() {

    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return upgradable.supportsInstallParameter(this.upgradeParameter)
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.installParameter(this.upgradeParameter, true)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.installParameter(this.upgradeParameter, false)
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
                        TextComponentString("Installs $upgradeParameter")
                                .setStyle(Style()
                                        .setColor(TextFormatting.WHITE))))
    }
}