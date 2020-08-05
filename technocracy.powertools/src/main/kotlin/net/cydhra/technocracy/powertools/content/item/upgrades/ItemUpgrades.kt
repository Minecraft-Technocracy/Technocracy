package net.cydhra.technocracy.powertools.content.item.upgrades

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.upgrades.ItemUpgrade
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.items.components.ItemMultiplierComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.api.upgrades.ItemMultiplierUpgrade
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.powertools.content.item.logic.*
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TextFormatting.GREEN
import java.util.*


/**
 * Upgrades energy capacity of something
 */
class CapacityUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ENERGY_CAPACITY)

/**
 * Upgrades energy consumption of items. All logic clients that consume energy, should respect an energy consumption
 * multiplier
 */
class EnergyEfficiencyUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ENERGY_USAGE)

/**
 * Upgrades digging speed of tools
 */
class DigSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_DIG_SPEED)

/**
 * Upgrades armor of armor using a multiplier. (duh)
 */
class ArmorUpgrade(additive: Double) : ItemMultiplierUpgrade(additive, UPGRADE_ARMOR_ARMOR)
class ArmorToughnessUpgrade(additive: Double) : ItemMultiplierUpgrade(additive, UPGRADE_ARMOR_TOUGHNESS)

/**
 * Upgrades a multiplier on walking speed of armor
 */
class WalkSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_WALK_SPEED)

/**
 * Upgrades an attack speed multiplier of an item
 */
class AttackSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ATTACK_SPEED)

/**
 * Upgrades an attack damage multiplier of an item
 */
class AttackDamageUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ATTACK_DAMAGE)

/**
 * A simple generic item upgrade. It can upgrade an item of the given upgrade parameter exactly once. It will install
 * a logic that is generated by [generator] upon installing the upgrade in an item and it will uninstall this logic,
 * if it is uninstalled from the item.
 */
open class SimpleItemUpgrade(
        override val upgradeParameter: UpgradeParameter,
        val name: String,
        val toolTip: ITextComponent,
        val generator: (ItemCapabilityWrapper, ItemUpgradesComponent) -> ILogic<ItemStackLogicParameters>
) : ItemUpgrade() {
    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return !upgradable.hasLogicStrategy(name)
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        upgradable.removeLogicStrategy(name)
    }

    override fun onUpgradeLoad(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        if (!upgradable.hasLogicStrategy(name))
            upgradable.addLogicStrategy(generator(upgradable, upgrades), name)
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(toolTip)
    }
}

/**
 * A generic upgrade for items. It can be installed multiple times. The installed logic gets a
 * [ItemMultiplierComponent] that gets the amount of installed equal upgrades as the multiplier, so the logic can
 * adjust its effects accordingly.
 */
open class StackableItemUpgrade(
        val name: String,
        val toolTip: ITextComponent,
        val generator: (ItemCapabilityWrapper, ItemUpgradesComponent, ItemMultiplierComponent) -> ILogic<ItemStackLogicParameters>
) : ItemUpgrade() {
    override val upgradeParameter: UpgradeParameter = UPGRADE_GENERIC

    override fun canInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent): Boolean {
        return true
    }

    override fun onInstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        (upgradable.getComponents().find { (name, _) -> name == "${this.name}Multiplier" }!!.second as
                ItemMultiplierComponent).multiplier -= 1.0
    }

    override fun onUpgradeLoad(upgradable: ItemCapabilityWrapper, upgrades: ItemUpgradesComponent) {
        val multiplier = if (!upgradable.hasLogicStrategy(this.name)) {
            val multiplier = ItemMultiplierComponent(null, 0.0, null)
            upgradable.registerComponent(multiplier, "${this.name}Multiplier")
            upgradable.addLogicStrategy(this.generator(upgradable, upgrades, multiplier), this.name)
            multiplier
        } else {
            upgradable.getComponents().find { (name, _) -> name == "${this.name}Multiplier" }!!.second as ItemMultiplierComponent
        }

        multiplier.multiplier += 1.0
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(toolTip)
    }
}

val fireExtinguishUpgrade = SimpleItemUpgrade(UPGRADE_GENERIC,
        "FireExtinguish",
        TextComponentTranslation("tooltips.upgrades.hint.antifire")
                .setStyle(Style().setColor(GREEN))) { _, _ ->
    FireExtinguishLogic()
}

val waterBreathingUpgrade = SimpleItemUpgrade(UPGRADE_GENERIC,
        "WaterElectrolyzer",
        TextComponentTranslation("tooltips.upgrades.hint.waterbreathing")
                .setStyle(Style().setColor(GREEN))) { _, _ ->
    WaterElectrolyzerLogic()
}

val nightVisionUpgrade = SimpleItemUpgrade(UPGRADE_GENERIC,
        "NightVision",
        TextComponentTranslation("tooltips.upgrades.hint.nightvision")
                .setStyle(Style().setColor(GREEN))) { _, _ ->
    NightVisionLogic()
}

val xpHarvestingUpgrade1 = SimpleItemUpgrade(UPGRADE_GENERIC,
        "XPHarvester1",
        TextComponentTranslation("tooltips.upgrades.hint.xpharvest").setStyle(Style().setColor(GREEN))
) { _, _ -> XPHarvesterUpgradeLogic(2f) }

val xpHarvestingUpgrade2 = SimpleItemUpgrade(UPGRADE_GENERIC,
        "XPHarvester2",
        TextComponentTranslation("tooltips.upgrades.hint.xpharvest").setStyle(Style().setColor(GREEN))
) { _, _ -> XPHarvesterUpgradeLogic(4f) }

val jetPackUpgrade = SimpleItemUpgrade(UPGRADE_GENERIC, "JetPack", TextComponentTranslation("tooltips.upgrades.hint.jetpack")
        .setStyle(Style().setColor(TextFormatting.GREEN))) { _, _ ->
    JetpackLogic()
}

/**
 * A stackable upgrade adding mining speed under water
 */
val aquaAffinityUpgrade = StackableItemUpgrade(
        "AquaAffinity",
        TextComponentTranslation("tooltips.upgrades.hint.aquaaffinity").setStyle(Style().setColor(GREEN))
) { _, _, multiplier -> AquaAffinityUpgradeLogic(multiplier) }

/**
 * A stackable upgrade adding 12% fall damage reduction per installed module
 */
val featherFallUpgrade = StackableItemUpgrade(
        "FeatherFall",
        TextComponentTranslation("tooltips.upgrades.hint.featherfall").setStyle(Style().setColor(GREEN))
) { _, _, multiplier -> NoFallLogic(multiplier) }
