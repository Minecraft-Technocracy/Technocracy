package net.cydhra.technocracy.foundation.api.upgrades

import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.minecraft.util.text.ITextComponent
import java.util.*

/**
 * An upgrade that performs a specific upgrade at its holder. This can be as simple as the modification of a specific
 * parameter, or complex behavior like additive consumption. An instance of this interface will always only upgrade a
 * singular functionality. It is the responsibility of upgrade items, to combine positive and negative upgrade effects.
 * @param U upgradable entity type (like TileEntity, or ItemStack)
 */
interface Upgrade<U> {

    val upgradeParameter: UpgradeParameter

    /**
     * @return true, iff the upgrade can be installed in the given tile entity.
     */
    fun canInstallUpgrade(upgradable: U, upgrades: MachineUpgradesTileEntityComponent): Boolean

    /**
     * Called when the upgrade is installed in the given tile entity.
     *
     * @param upgradable the entity that this upgrade is installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    fun onInstallUpgrade(upgradable: U, upgrades: MachineUpgradesTileEntityComponent)

    /**
     * Called when the upgrade is uninstalled from the given tile entity
     *
     * @param upgradable the entity that this upgrade was installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    fun onUninstallUpgrade(upgradable: U, upgrades: MachineUpgradesTileEntityComponent)

    /**
     * Called when the machine that holds the upgrade is loaded from NBT
     */
    fun onUpgradeLoad(upgradable: U, upgrades: MachineUpgradesTileEntityComponent)

    /**
     * Get an optional upgrade description text for items for special upgrades that need explanation
     */
    fun getUpgradeDescription(): Optional<ITextComponent>
}