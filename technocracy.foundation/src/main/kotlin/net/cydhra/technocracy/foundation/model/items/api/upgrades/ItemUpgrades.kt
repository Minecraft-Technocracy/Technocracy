package net.cydhra.technocracy.foundation.model.items.api.upgrades

import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.upgrades.BaseUpgrade
import net.cydhra.technocracy.foundation.model.upgrades.IUpgradeClass
import net.cydhra.technocracy.foundation.model.upgrades.BaseMultiplierUpgrade
import net.cydhra.technocracy.foundation.model.upgrades.UpgradeParameter
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import java.util.*


/**
 * Models instances of upgrades that can be granted by upgrade items. Does not take any parameters, as different
 * upgrades may encompass different approaches on how they improve their item: One might add a multiplier on a
 * value, another one may just unlock a new mechanic, thus not requiring parameters in its instance.
 */
abstract class ItemUpgrade(upgradeType: UpgradeParameter) : BaseUpgrade<UpgradeParameter, ItemStack, ItemUpgradesComponent>(upgradeType)

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class ItemMultiplierUpgrade(multiplier: Double, parameterName: UpgradeParameter)
    : BaseMultiplierUpgrade<UpgradeParameter, ItemStack, ItemUpgradesComponent>(multiplier, parameterName) {
    override fun canInstallUpgrade(target: ItemStack,
                                   upgrades: ItemUpgradesComponent): Boolean {
        return true
    }

    override fun onInstallUpgrade(target: ItemStack, upgrades: ItemUpgradesComponent) {
    }

    override fun onUninstallUpgrade(target: ItemStack, upgrades: ItemUpgradesComponent) {
    }

    override fun onUpgradeLoad(target: ItemStack, upgrades: ItemUpgradesComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.empty()
    }
}

/**
 * Every upgrade item has a class assigned and certain machines may only accept certain upgrade classes (so a
 * pulverizer is not upgradeable with a "semipermeable membrane" or something of that kind)
 */
enum class ItemUpgradeClass(private val internalUnlocalizedName: String) : IUpgradeClass {
    MECHANICAL("mechanical"),
    ELECTRICAL("electrical"),
    COMPUTER("computer"),
    OPTICAL("optical"),
    THERMAL("thermal"),
    CHEMICAL("chemical"),
    NUCLEAR("nuclear"),
    ALIEN("alien");

    override fun getUnlocalizedName(): String {
        return internalUnlocalizedName
    }
}