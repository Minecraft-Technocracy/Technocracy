package net.cydhra.technocracy.foundation.tileentity.machines

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.logic.ItemProcessingLogic
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityChemicalReactionChamber : MachineTileEntity() {
    private val inputInventoryComponent = FluidComponent(4000,
            tanktype = DynamicFluidHandler.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))
    private val inputInventoryComponent2 = FluidComponent(4000,
            tanktype = DynamicFluidHandler.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))

    private val outputInventoryComponent = FluidComponent(4000,
            tanktype = DynamicFluidHandler.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    init {
        this.registerComponent(inputInventoryComponent, "input_1")
        this.registerComponent(inputInventoryComponent2, "input_2")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.CHEMICAL_REACTION,
                inputFluidSlots = arrayOf(this.inputInventoryComponent.fluid, this.inputInventoryComponent2.fluid),
                outputFluidSlots = arrayOf(this.outputInventoryComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 40,
                progress = this.progressComponent
        ))
    }
}