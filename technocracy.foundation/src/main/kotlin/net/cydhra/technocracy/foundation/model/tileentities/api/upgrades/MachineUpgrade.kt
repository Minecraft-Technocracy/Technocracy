package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

/**
 * Models instances of upgrades that can be granted by upgrade items. Does not take any parameters, as different
 * upgrades may encompass different approaches on how they improve their machine: One might add a multiplier on a
 * value, another one may just unlock a new slot in the machine, thus not requiring parameters in its instance.
 */
abstract class MachineUpgrade(val upgradeType: MachineUpgradeParameter)

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class MultiplierUpgrade(val multiplier: Double, parameterName: MachineUpgradeParameter) : MachineUpgrade(parameterName)

/**
 * Models exactly one parameter of a machine that can be modified. Actual upgrade items likely modify multiple
 * parameters, either positively or negatively.
 */
typealias MachineUpgradeParameter = String

/**
 * Every upgrade item has a class assigned and certain machines may only accept certain upgrade classes (so a
 * pulverizer is not upgradeable with a "semipermeable membrane" or something of that kind)
 */
enum class MachineUpgradeClass {
    MECHANICAL,
    ELECTRICAL,
    MAGNETIC,
    OPTICAL,
    THERMAL,
    CHEMICAL,
    NUCLEAR,
    ALIEN
}