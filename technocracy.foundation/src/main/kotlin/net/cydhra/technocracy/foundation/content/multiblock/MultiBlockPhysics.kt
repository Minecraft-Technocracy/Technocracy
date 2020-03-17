package net.cydhra.technocracy.foundation.content.multiblock

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.data.config.DoubleConfigurable
import net.cydhra.technocracy.foundation.data.config.IntegerConfigurable
import net.minecraftforge.common.config.Configuration

/**
 * This object collects configuration options about different physics related in-game parameters. They are collected
 * here, so they can be initialized and added to the configuration at start of game, so the configuration file is
 * complete.
 */
object MultiBlockPhysics {

    private const val BOILER_CATEGORY = "structures${Configuration.CATEGORY_SPLITTER}boiler"
    private const val SALINE_CATEGORY = "structures${Configuration.CATEGORY_SPLITTER}saline"

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
            4,
            "Temperature of heaters and conductors. The heat spreads with a falloff of 1 to neighbor blocks. Every " +
                    "heated block consumes the same energy and produces steam based of its heat value", 1, 20)

    /**
     * The base steam gen of heated blocks in the boilers interior.
     */
    val baseSteamGeneration by IntegerConfigurable(TCFoundation.config, BOILER_CATEGORY, "baseSteamGeneration",
            2, "How much steam a heated block inside the boiler produces per level of heat.", 1, 100)

    /**
     * The amount of milli-Heat each tile of a saline can store.
     */
    val salineHeatCapacityPerTile by IntegerConfigurable(TCFoundation.config, SALINE_CATEGORY, "heatCapacityPerTile",
            500_000, "The amount of milli-Heat each tile of a saline can store.", 1000,
            100_000_000)

    /**
     * The amount of heat that is lost each tick in percent.
     */
    val salineHeatLoss by DoubleConfigurable(TCFoundation.config, SALINE_CATEGORY, "heatLoss", 0.0001,
            "The amount of heat that is lost each tick in percent. A value of 1 would mean that 100% of the heat is lost each tick.")

    /**
     * The amount of heating fluid that is converted to cold fluid each tick per tile.
     */
    val salineHeatingAgentConversionSpeed by IntegerConfigurable(TCFoundation.config, SALINE_CATEGORY,
            "heatingAgentConversionSpeed", 1,
            "The amount of heating fluid that is converted to cold fluid each tick per tile.", 1, 1000)

    /**
     * A factor used in a more complex equation to calculate the amount of heat that is used each tick per tile.
     */
    val salineHeatDrainPerTile by DoubleConfigurable(TCFoundation.config, SALINE_CATEGORY, "heatDrainPerTile", 0.02,
            "A factor used in a more complex equation to calculate the amount of heat that is used each tick per tile.")

    /**
     * The base amount of fluid that is converted each tick per tile.
     */
    val salineBaseConversionPerTile by IntegerConfigurable(TCFoundation.config, SALINE_CATEGORY,
            "baseConversionPerTile", 1, "The base amount of fluid that is converted each tick per tile.", 1, 10_000)

    fun initialize() {
        // all configuration is already initialized when this class is loaded. This method mainly exists to have a
        // expressive way to force load the class
    }
}