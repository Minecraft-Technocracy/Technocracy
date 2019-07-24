package net.cydhra.technocracy.foundation.multiblock

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.config.IntegerConfigurable
import net.minecraftforge.common.config.Configuration

object MultiBlockPhysics {

    private const val BOILER_CATEGORY = "structures${Configuration.CATEGORY_SPLITTER}boiler"

    /**
     * the base energy usage of one heated block in the interior
     */
    val baseEnergyUsage by IntegerConfigurable(TCFoundation.config, BOILER_CATEGORY, "baseEnergyUsage", 20, "How " +
            "much energy the boiler consumes per tick per heated block", 1, 2000)

    /**
     * The temperature of the heat conductors. It spreads with a falloff of 1 to nearby blocks. Every block that
     * has a temperature multiplier of more than zero produces [baseSteamGeneration] * [conductorTemperature]
     */
    val conductorTemperature by IntegerConfigurable(TCFoundation.config, BOILER_CATEGORY, "conductorTemperature",
            4, "Temperature of heaters and conductors. The heat spreads with a falloff of 1 to neighbor blocks. Every " +
            "heated block consumes the same energy and produces steam based of its heat value", 1, 20)

    /**
     * The base steam gen of heated blocks in the boilers interior.
     */
    val baseSteamGeneration by IntegerConfigurable(TCFoundation.config, BOILER_CATEGORY, "baseSteamGeneration",
            2, "How much steam a heated block inside the boiler produces per level of heat.", 1, 100)

    fun initialize() {
        // all configuration is already initialized when this class is loaded. This method mainly exists to have a
        // expressive way to force load the class
    }
}