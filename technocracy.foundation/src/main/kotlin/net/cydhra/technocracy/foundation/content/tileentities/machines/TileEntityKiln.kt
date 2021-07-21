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
class TileEntityKiln : MachineTileEntity() {

    private val inputInventoryComponent = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val outputInventoryComponent = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.KILN,
                inputFluidSlots = arrayOf(this.inputInventoryComponent.fluid),
                outputFluidSlots = arrayOf(this.outputInventoryComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 40,
                progress = this.progressComponent
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }
}