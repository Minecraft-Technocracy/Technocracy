package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

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