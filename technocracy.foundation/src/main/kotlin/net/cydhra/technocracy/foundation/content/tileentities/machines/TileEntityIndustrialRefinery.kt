package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_SPEED
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityIndustrialRefinery : MachineTileEntity(), TEInventoryProvider {
    private val inputFluidComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val inputItemComponent = InventoryTileEntityComponent(2, this, EnumFacing.NORTH,
            DynamicInventoryCapability.InventoryType.BOTH)

    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.THERMAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent))

    /**
     * All recipes of the industrial refinery; loaded lazily so they are not loaded before game loop, as they
     * might not have been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.INDUSTRIAL_REFINERY) ?: emptyList())
    }

    init {
        this.registerComponent(inputFluidComponent, "fluid")
        this.registerComponent(inputItemComponent, "item")
        this.registerComponent(outputInventoryComponent, "output")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(ItemProcessingLogic(
                RecipeManager.RecipeType.INDUSTRIAL_REFINERY,
                inputInventory = inputItemComponent.inventory,
                outputInventory = outputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                progress = this.progressComponent,
                baseTickEnergyCost = 200
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return if (inventory == this.inputItemComponent.inventory) {
            this.recipes.any { it.getInput()[slot].test(stack) }
        } else {
            false
        }
    }
}