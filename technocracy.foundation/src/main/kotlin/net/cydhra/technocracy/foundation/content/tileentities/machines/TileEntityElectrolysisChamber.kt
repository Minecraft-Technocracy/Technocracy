package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityElectrolysisChamber : MachineTileEntity() {
    private val inputInventoryComponent = FluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val outputInventoryComponent1 = FluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    private val outputInventoryComponent2 = FluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.SOUTH))

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent1, "output_inventory1")
        this.registerComponent(outputInventoryComponent2, "output_inventory2")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.ELECTROLYSIS,
                inputFluidSlots = arrayOf(this.inputInventoryComponent.fluid),
                outputFluidSlots = arrayOf(this.outputInventoryComponent1.fluid, this.outputInventoryComponent2.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 80,
                progress = this.progressComponent
        ))
    }
}