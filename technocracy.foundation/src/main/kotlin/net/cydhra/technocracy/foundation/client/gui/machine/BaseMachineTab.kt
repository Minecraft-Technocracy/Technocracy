package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.CoolantMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.LubricantFluidMeter
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.CoolingUpgrade
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.LubricantUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation

// nullable platform type: its null if the te is not placed in the world (required for jei)
@Suppress("UNNECESSARY_SAFE_CALL")
abstract class BaseMachineTab(val machine: MachineTileEntity, parent: TCGui, icon: TCIcon = TCIcon(machine?.blockType
        ?: Blocks.AIR)) :
        TCTab(name = machine.blockType?.localizedName ?: "", parent = parent, icon = icon) {

    override fun init() {
        val upgardeComp = machine.getComponents().stream().filter { it.second is MachineUpgradesTileEntityComponent }.findFirst()
        if (upgardeComp.isPresent) {
            val upgrades = (upgardeComp.get().second as MachineUpgradesTileEntityComponent).getInstalledUpgrades()
            val hasCooling = upgrades.find { it is CoolingUpgrade } != null
            val hasLubricant = upgrades.find { it is LubricantUpgrade } != null

            var yOff = 20 - 7
            if (hasLubricant) {
                val compLubricant = machine.getComponents().find { it.first == LubricantUpgrade.LUBRICANT_FLUID_COMPONENT_NAME }
                if(compLubricant != null) {
                    yOff += components.addElement(LubricantFluidMeter(-36, yOff , compLubricant.second as FluidTileEntityComponent, parent)).height
                    yOff += 5
                }
            }

            if (hasCooling) {
                val compCoolIn = machine.getComponents().find { it.first == CoolingUpgrade.COOLER_FLUID_INPUT_NAME }
                val compCoolOut = machine.getComponents().find { it.first == CoolingUpgrade.COOLER_FLUID_OUTPUT_NAME }
                val compHeat = machine.getComponents().find { it.first == CoolingUpgrade.COOLER_HEAT_STORAGE_COMPONENT_NAME }
                if (compCoolIn != null && compCoolOut != null && compHeat != null) {
                    components.add(CoolantMeter(-64, yOff , compCoolIn.second as FluidTileEntityComponent, compCoolOut.second as FluidTileEntityComponent, compHeat.second as HeatStorageTileEntityComponent, parent))
                }
            }
        }
    }
}

