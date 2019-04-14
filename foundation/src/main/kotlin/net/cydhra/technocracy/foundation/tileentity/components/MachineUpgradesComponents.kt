package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound

/**
 * A machine component that handles all machine upgrades.
 */
class MachineUpgradesComponents : IComponent {

    companion object {
        private const val NBT_KEY_UPGRADES = "upgrades"
    }

    /**
     * Allowed Upgrades and maximum amount
     */
    private val allowedUpgrades = HashMap<UpgradeType, Int>()

    /**
     * Installed Upgrades with amount
     */
    private val installedUpgrades = HashMap<UpgradeType, Int>()

    override fun serializeNBT(): NBTBase {
        val tag = NBTTagCompound()

        for ((upgrade, amount) in installedUpgrades) {
            tag.setInteger(upgrade.name, amount)
        }

        return tag
    }

    override fun deserializeNBT(nbt: NBTBase) {
        for (upgrade in UpgradeType.values()) {
            if ((nbt as NBTTagCompound).hasKey(upgrade.name)) {
                installedUpgrades[upgrade] = nbt.getInteger(upgrade.name)
            }
        }
    }

    /**
     * Install an Upgrade, returns amount of added Upgrades
     */
    fun setUpgrades(upgradeType: UpgradeType, amount: Int) {
        val max = allowedUpgrades.getOrDefault(upgradeType, -1)

        if (max != -1) {
            installedUpgrades[upgradeType] = Math.min(max, amount)
        }
    }

    /**
     * Install an Upgrade, returns true if successful
     */
    fun addUpgrade(upgradeType: UpgradeType): Boolean {
        return addUpgrade(upgradeType, 1) != 0;
    }

    /**
     * Install an Upgrade, returns amount of added Upgrades
     */
    fun addUpgrade(upgradeType: UpgradeType, amount: Int): Int {

        val max = allowedUpgrades.getOrDefault(upgradeType, -1)

        if (max != -1) {
            val current = installedUpgrades.getOrDefault(upgradeType, 0)

            return if (current + amount > max) {
                installedUpgrades[upgradeType] = max
                max - current
            } else {
                installedUpgrades[upgradeType] = current + amount
                amount
            }
        }
        return 0
    }

    /**
     * Install an Upgrade, returns true if successful
     */
    fun removeUpgrade(upgradeType: UpgradeType): Boolean {
        return removeUpgrade(upgradeType, 1) != 0;
    }

    /**
     * Install an Upgrade, returns amount of removed Upgrades
     */
    fun removeUpgrade(upgradeType: UpgradeType, amount: Int): Int {
        if (allowedUpgrades.containsKey(upgradeType)) {
            val current = installedUpgrades.getOrDefault(upgradeType, 0)
            if (current != 0) {
                return if (current - amount >= 0) {
                    installedUpgrades[upgradeType] = current - amount
                    amount
                } else {
                    installedUpgrades[upgradeType] = 0
                    current
                }
            }
        }
        return 0
    }

    /**
     * The type of upgrade for the machine.
     */
    enum class UpgradeType {
        /**
         * Machine speed upgrade. This upgrade increases machine speed while also increasing energy usage
         */
        SPEED,

        /**
         * Machine energy upgrade. This decreases energy usage but increases usage of auxiliary liquid, if present
         */
        ENERGY,

        /**
         * Machine upgrade reducing usage of auxiliary gas or liquid, but increases heat generation if present
         */
        AUXILIARY_USAGE,

        /**
         * Machine upgrade reducing generation of heat. TODO: negative effects of this upgrade?
         */
        HEAT
    }
}