package net.cydhra.technocracy.foundation.model.items.api

import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MultiplierUpgrade
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.text.*
import net.minecraft.world.World
import java.util.*

/**
 * Every item that can be used as an machine upgrade, derives from this base class. Upgrades are not stackable. This
 * does not have technical reasons, but is merely to avoid confusion of the player, as upgrades in machines aren't
 * stackable within the upgrade slots either. (Stacking upgrades by placing them in multiple slots can work,
 * depending on what the upgrade actually does)
 *
 * @param upgradeClass the [MachineUpgradeClass] this item belongs to
 * @param upgrades a list of upgrades that are applied to the machine this item is installed in
 */
class UpgradeItem(unlocalizedName: String,
                  val upgradeClass: MachineUpgradeClass,
                  vararg val upgrades: MachineUpgrade) : BaseItem(unlocalizedName) {
    init {
        maxStackSize = 1
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(
                TextComponentTranslation("tooltips.upgrades.title.class")
                        .setStyle(Style()
                                .setColor(TextFormatting.GREEN))
                        .appendSibling(TextComponentString(": ")
                                .setStyle(Style()
                                        .setColor(TextFormatting.GREEN)))
                        .appendSibling(
                                TextComponentTranslation("tooltips.upgrades.class.${upgradeClass.unlocalizedName}")
                                        .setStyle(Style()
                                                .setColor(TextFormatting.GOLD)))
                        .formattedText)

        this.upgrades
                .filterIsInstance<MultiplierUpgrade>()
                .forEach { upgrade ->
                    tooltip.add(
                            TextComponentTranslation("tooltips.upgrades.parameter.${upgrade.upgradeType}")
                                    .setStyle(Style()
                                            .setColor(TextFormatting.AQUA))
                                    .appendSibling(TextComponentString(": ")
                                            .setStyle(Style()
                                                    .setColor(TextFormatting.AQUA)))
                                    .appendSibling(
                                            TextComponentString("${if (upgrade.multiplier > 0)
                                                "+"
                                            else
                                                ""}${(upgrade.multiplier * 100).toInt()}%")
                                                    .setStyle(Style()
                                                            .setColor(TextFormatting.WHITE)))
                                    .formattedText)
                }

        // append custom tooltips of special upgrades
        this.upgrades
                .map(MachineUpgrade::getUpgradeDescription)
                .filter(Optional<ITextComponent>::isPresent)
                .map(Optional<ITextComponent>::get)
                .forEach { tooltip.add(it.formattedText) }
    }
}