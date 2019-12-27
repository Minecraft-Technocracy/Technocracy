package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_SPEED
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityChemicalReactionChamber : MachineTileEntity() {
    private val inputInventoryComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))
    private val inputInventoryComponent2 = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))

    private val outputInventoryComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent))

    init {
        this.registerComponent(inputInventoryComponent, "input_1")
        this.registerComponent(inputInventoryComponent2, "input_2")
        this.registerComponent(outputInventoryComponent, "output_inventory")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.CHEMICAL_REACTION,
                inputFluidSlots = arrayOf(this.inputInventoryComponent.fluid, this.inputInventoryComponent2.fluid),
                outputFluidSlots = arrayOf(this.outputInventoryComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 80,
                progress = this.progressComponent
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }
}