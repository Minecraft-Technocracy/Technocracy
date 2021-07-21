package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.CoolantMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.LubricantMeter
import net.cydhra.technocracy.foundation.content.tileentities.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityMachineUpgradesComponent
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.CoolingUpgrade
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.LubricantUpgrade
import net.minecraft.init.Blocks

// nullable platform type: its null if the te is not placed in the world (required for jei)
@Suppress("UNNECESSARY_SAFE_CALL")
abstract class BaseMachineTab(val machine: MachineTileEntity, parent: TCGui, icon: TCIcon = TCIcon(machine?.blockType
        ?: Blocks.AIR)) :
        TCTab(name = machine.blockType?.localizedName ?: "", parent = parent, icon = icon) {

    override fun init() {
        val upgardeComp = machine.getComponents().stream().filter { it.second is TileEntityMachineUpgradesComponent }.findFirst()
        if (upgardeComp.isPresent) {
            val upgrades = (upgardeComp.get().second as TileEntityMachineUpgradesComponent).getInstalledUpgrades()
            val hasCooling = upgrades.find { it is CoolingUpgrade } != null
            val hasLubricant = upgrades.find { it is LubricantUpgrade } != null

            var yOff = 20 - 7
            if (hasLubricant) {
                val compLubricant = machine.getComponents().find { it.first == LubricantUpgrade.LUBRICANT_FLUID_COMPONENT_NAME }
                if(compLubricant != null) {
                    yOff += components.addElement(
                        LubricantMeter(
                            -36,
                            yOff,
                            compLubricant.second as TileEntityFluidComponent,
                            parent
                        )
                    ).height
                    yOff += 5
                }
            }

            if (hasCooling) {
                val compCoolIn = machine.getComponents().find { it.first == CoolingUpgrade.COOLER_FLUID_INPUT_NAME }
                val compCoolOut = machine.getComponents().find { it.first == CoolingUpgrade.COOLER_FLUID_OUTPUT_NAME }
                val compHeat = machine.getComponents().find { it.first == CoolingUpgrade.COOLER_HEAT_STORAGE_COMPONENT_NAME }
                if (compCoolIn != null && compCoolOut != null && compHeat != null) {
                    components.add(CoolantMeter(-64, yOff , compCoolIn.second as TileEntityFluidComponent, compCoolOut.second as TileEntityFluidComponent, compHeat.second as TileEntityHeatStorageComponent, parent))
                }
            }
        }
    }
}

