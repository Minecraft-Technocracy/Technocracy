package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

/**
 * Models instances of upgrades that can be granted by upgrade items. Does not take any parameters, as different
 * upgrades may encompass different approaches on how they improve their machine: One might add a multiplier on a
 * value, another one may just unlock a new slot in the machine, thus not requiring parameters in its instance.
 */
abstract class MachineUpgrade(val upgradeType: MachineUpgradeParameter)

/**
 * Models exactly one parameter of a machine that can be modified. Actual upgrade items likely modify multiple
 * parameters, either positively or negatively.
 */
typealias MachineUpgradeParameter = String