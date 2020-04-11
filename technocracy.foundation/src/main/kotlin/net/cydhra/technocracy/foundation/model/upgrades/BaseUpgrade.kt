package net.cydhra.technocracy.foundation.model.upgrades

import net.minecraft.util.text.ITextComponent
import java.util.*


abstract class BaseUpgrade<UpgradeType, Target, UpgradeComponent>(val upgradeType: UpgradeType) {
    /**
     * @return true, if the upgrade can be installed in the given target.
     */
    abstract fun canInstallUpgrade(target: Target,
                                   upgrades: UpgradeComponent): Boolean

    /**
     * Called when the upgrade is installed in the given target.
     *
     * @param target the tile entity that this upgrade is installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    abstract fun onInstallUpgrade(target: Target,
                                  upgrades: UpgradeComponent)

    /**
     * Called when the upgrade is uninstalled from the given target
     *
     * @param target the tile entity that this upgrade was installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    abstract fun onUninstallUpgrade(target: Target,
                                    upgrades: UpgradeComponent)

    /**
     * Called when the target that holds the upgrade is loaded from NBT
     */
    abstract fun onUpgradeLoad(target: Target,
                               upgrades: UpgradeComponent)

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
abstract class BaseMultiplierUpgrade<UpgradeType, Target, UpgradeComponent>(var multiplier: Double, parameterName: UpgradeType)
    : BaseUpgrade<UpgradeType, Target, UpgradeComponent>(parameterName) {
    override fun canInstallUpgrade(target: Target,
                                   upgrades: UpgradeComponent): Boolean {
        return true
    }

    override fun onInstallUpgrade(target: Target, upgrades: UpgradeComponent) {
    }

    override fun onUninstallUpgrade(target: Target, upgrades: UpgradeComponent) {
    }

    override fun onUpgradeLoad(target: Target, upgrades: UpgradeComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.empty()
    }
}

/**
 * Models exactly one parameter of a machine that can be modified. Actual upgrade items likely modify multiple
 * parameters, either positively or negatively.
 */
typealias UpgradeParameter = String

interface IUpgradeClass {
    fun getUnlocalizedName(): String
}