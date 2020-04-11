package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

import net.cydhra.technocracy.foundation.api.tileentities.TCMachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.model.upgrades.BaseMultiplierUpgrade
import net.cydhra.technocracy.foundation.model.upgrades.BaseUpgrade
import net.cydhra.technocracy.foundation.model.upgrades.IUpgradeClass
import net.cydhra.technocracy.foundation.model.upgrades.UpgradeParameter
import net.minecraft.util.text.ITextComponent
import java.util.*


/**
 * Models instances of upgrades that can be granted by upgrade items. Does not take any parameters, as different
 * upgrades may encompass different approaches on how they improve their machine: One might add a multiplier on a
 * value, another one may just unlock a new slot in the machine, thus not requiring parameters in its instance.
 */
abstract class MachineUpgrade(upgradeType: UpgradeParameter) : BaseUpgrade<UpgradeParameter, TCMachineTileEntity, MachineUpgradesTileEntityComponent>(upgradeType)

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class TileEntityMultiplierUpgrade(multiplier: Double, parameterName: UpgradeParameter)
    : BaseMultiplierUpgrade<UpgradeParameter, TCMachineTileEntity, MachineUpgradesTileEntityComponent>(multiplier, parameterName) {
    override fun canInstallUpgrade(target: TCMachineTileEntity,
                                   upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return true
    }

    override fun onInstallUpgrade(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun onUninstallUpgrade(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun onUpgradeLoad(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.empty()
    }
}


/**
 * Every upgrade item has a class assigned and certain machines may only accept certain upgrade classes (so a
 * pulverizer is not upgradeable with a "semipermeable membrane" or something of that kind)
 */
enum class MachineUpgradeClass(private val internalUnlocalizedName: String) : IUpgradeClass {
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