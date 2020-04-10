package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

import net.cydhra.technocracy.foundation.api.upgrades.Upgradable
import net.cydhra.technocracy.foundation.api.upgrades.Upgrade
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.minecraft.util.text.ITextComponent
import java.util.*

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class MultiplierUpgrade(
        val multiplier: Double,
        override val upgradeParameter: UpgradeParameter
) : Upgrade<Upgradable> {

    override fun canInstallUpgrade(upgradable: Upgradable,
                                   upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgradable.supportsParameter(this.upgradeParameter)
    }

    override fun onInstallUpgrade(upgradable: Upgradable, upgrades: MachineUpgradesTileEntityComponent) {
        upgradable.upgradeParameter(this.upgradeParameter, this.multiplier)
    }

    override fun onUninstallUpgrade(upgradable: Upgradable, upgrades: MachineUpgradesTileEntityComponent) {
        upgradable.upgradeParameter(this.upgradeParameter, -this.multiplier)
    }

    override fun onUpgradeLoad(upgradable: Upgradable, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.empty()
    }
}