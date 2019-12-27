package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.AdditiveConsumptionLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ADDITIVE_CONSUMPTION
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_SPEED
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityPolymerizationChamber : MachineTileEntity(), TEInventoryProvider {
    private val inputFluidComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val additiveFluidComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))

    private val additiveMultiplierComponent = MultiplierTileEntityComponent(MACHINE_UPGRADE_ADDITIVE_CONSUMPTION)

    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_ADDITIVE_CONSUMPTION,
                    MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.THERMAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent, this.additiveMultiplierComponent))

    init {
        this.registerComponent(inputFluidComponent, "input")
        this.registerComponent(outputInventoryComponent, "output")
        this.registerComponent(additiveFluidComponent, "additive")
        this.registerComponent(additiveMultiplierComponent, "additive_usage")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(AdditiveConsumptionLogic(additiveFluidComponent, 5, additiveMultiplierComponent))
        this.addLogicStrategy(ItemProcessingLogic(
                RecipeManager.RecipeType.POLYMERIZATION,
                outputInventory = outputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                progress = this.progressComponent,
                baseTickEnergyCost = 100
        ))
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return false
    }
}