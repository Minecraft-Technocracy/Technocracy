package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.tileentities.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityElectrolysisChamber : MachineTileEntity() {
    private val inputInventoryComponent = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val outputInventoryComponent1 = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    private val outputInventoryComponent2 = TileEntityFluidComponent(4000,
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
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 80,
                progress = this.progressComponent
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }
}