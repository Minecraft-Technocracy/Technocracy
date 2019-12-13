package net.cydhra.technocracy.foundation.model.items.api

import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgrade

/**
 * Every item that can be used as an machine upgrade, derives from this base class. Upgrades are not stackable. This
 * does not have technical reasons, but is merely to avoid confusion of the player, as upgrades in machines aren't
 * stackable within the upgrade slots either. (Stacking upgrades by placing them in multiple slots can work,
 * depending on what the upgrade actually does)
 */
class UpgradeItem(unlocalizedName: String, vararg val upgrades: MachineUpgrade) : BaseItem(unlocalizedName) {
    init {
        maxStackSize = 1
    }
}