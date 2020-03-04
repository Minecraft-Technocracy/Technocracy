package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCMachineTileEntity
import net.minecraft.util.text.ITextComponent
import java.util.*

/**
 * Models instances of upgrades that can be granted by upgrade items. Does not take any parameters, as different
 * upgrades may encompass different approaches on how they improve their machine: One might add a multiplier on a
 * value, another one may just unlock a new slot in the machine, thus not requiring parameters in its instance.
 */
abstract class MachineUpgrade(val upgradeType: MachineUpgradeParameter) {

    /**
     * @return true, iff the upgrade can be installed in the given tile entity.
     */
    abstract fun canInstallUpgrade(tile: TCMachineTileEntity,
            upgrades: MachineUpgradesTileEntityComponent): Boolean

    /**
     * Called when the upgrade is installed in the given tile entity.
     *
     * @param tile the tile entity that this upgrade is installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    abstract fun onInstallUpgrade(tile: TCMachineTileEntity,
            upgrades: MachineUpgradesTileEntityComponent)

    /**
     * Called when the upgrade is uninstalled from the given tile entity
     *
     * @param tile the tile entity that this upgrade was installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    abstract fun onUninstallUpgrade(tile: TCMachineTileEntity,
                                    upgrades: MachineUpgradesTileEntityComponent)

    /**
     * Called when the machine that holds the upgrade is loaded from NBT
     */
    abstract fun onUpgradeLoad(tile: TCMachineTileEntity,
                               upgrades: MachineUpgradesTileEntityComponent)

    /**
     * Get an optional upgrade description text for items for special upgrades that need explanation
     */
    abstract fun getUpgradeDescription(): Optional<ITextComponent>
}

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class MultiplierUpgrade(val multiplier: Double, parameterName: MachineUpgradeParameter)
    : MachineUpgrade(parameterName) {
    override fun canInstallUpgrade(tile: TCMachineTileEntity,
            upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return true
    }

    override fun onInstallUpgrade(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun onUninstallUpgrade(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun onUpgradeLoad(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.empty()
    }
}

/**
 * Models exactly one parameter of a machine that can be modified. Actual upgrade items likely modify multiple
 * parameters, either positively or negatively.
 */
typealias MachineUpgradeParameter = String

/**
 * Every upgrade item has a class assigned and certain machines may only accept certain upgrade classes (so a
 * pulverizer is not upgradeable with a "semipermeable membrane" or something of that kind)
 */
enum class MachineUpgradeClass(val unlocalizedName: String) {
    MECHANICAL("mechanical"),
    ELECTRICAL("electrical"),
    COMPUTER("computer"),
    OPTICAL("optical"),
    THERMAL("thermal"),
    CHEMICAL("chemical"),
    NUCLEAR("nuclear"),
    ALIEN("alien");
}