package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound


class MachineUpgrades : IComponent {

    /**
     * Allowed Upgrades and maximum amount
     */
    private val allowedUpgrades = HashMap<Upgrade, Int>()
    /**
     * Installed Upgrades with amount
     */
    private val installedUpgrades = HashMap<Upgrade, Int>()

    override fun readFromNBT(nbtTags: NBTTagCompound) {
        if (nbtTags.hasKey("upgrades")) {
            val upgrades = nbtTags.getCompoundTag("upgrades")

            for (u in Upgrade.values()) {
                if (upgrades.hasKey(u.name)) {
                    installedUpgrades.put(u, upgrades.getInteger(u.name))
                }
            }
        }
    }

    override fun writeToNBT(nbtTags: NBTTagCompound) {
        val tag = NBTTagCompound()

        for ((upgrade, amount) in installedUpgrades) {
            tag.setInteger(upgrade.name, amount)
        }

        nbtTags.setTag("upgrades", tag)
    }

    /**
     * Install an Upgrade, returns amount of added Upgrades
     */
    fun setUpgrades(upgrade: Upgrade, amount: Int) {
        val max = allowedUpgrades.getOrDefault(upgrade, -1)

        if (max != -1) {
            installedUpgrades.put(upgrade, Math.min(max, amount))
        }
    }

    /**
     * Install an Upgrade, returns true if successful
     */
    fun addUpgrade(upgrade: Upgrade): Boolean {
        return addUpgrade(upgrade, 1) != 0;
    }

    /**
     * Install an Upgrade, returns amount of added Upgrades
     */
    fun addUpgrade(upgrade: Upgrade, amount: Int): Int {

        val max = allowedUpgrades.getOrDefault(upgrade, -1)

        if (max != -1) {
            val current = installedUpgrades.getOrDefault(upgrade, 0)

            return if (current + amount > max) {
                installedUpgrades[upgrade] = max
                max - current
            } else {
                installedUpgrades[upgrade] = current + amount
                amount
            }
        }
        return 0
    }

    /**
     * Install an Upgrade, returns true if successful
     */
    fun removeUpgrade(upgrade: Upgrade): Boolean {
        return removeUpgrade(upgrade, 1) != 0;
    }

    /**
     * Install an Upgrade, returns amount of removed Upgrades
     */
    fun removeUpgrade(upgrade: Upgrade, amount: Int): Int {
        if (allowedUpgrades.containsKey(upgrade)) {
            val current = installedUpgrades.getOrDefault(upgrade, 0)
            if (current != 0) {
                if (current - amount >= 0) {
                    installedUpgrades[upgrade] = current - amount
                    return amount
                } else {
                    installedUpgrades[upgrade] = 0
                    return current
                }
            }
        }
        return 0
    }

    enum class Upgrade {
        SPEED, ENERGY
    }
}