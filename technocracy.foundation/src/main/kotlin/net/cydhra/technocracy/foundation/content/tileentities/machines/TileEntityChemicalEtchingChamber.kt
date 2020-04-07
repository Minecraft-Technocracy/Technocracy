package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.machine.BaseMachineTab
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.fluids.hydrochloricAcidFluid
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
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityChemicalEtchingChamber : MachineTileEntity(), TEInventoryProvider {
    private val inputInventory = InventoryTileEntityComponent(3, this, EnumFacing.EAST)
    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.WEST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val additiveMultiplierComponent = MultiplierTileEntityComponent(MACHINE_UPGRADE_ADDITIVE_CONSUMPTION)

    private val acidFluidInput = FluidTileEntityComponent(4000, hydrochloricAcidFluid.name,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.NORTH))

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_ADDITIVE_CONSUMPTION,
                    MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.OPTICAL,
                    MachineUpgradeClass.ELECTRICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent, this.additiveMultiplierComponent))

    /**
     * All recipes of the etching chamber; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.CHEMICAL_ETCHING) ?: emptyList())
    }

    init {
        registerComponent(inputInventory, "input")
        registerComponent(outputInventoryComponent, "output")
        registerComponent(acidFluidInput, "acid")
        registerComponent(additiveMultiplierComponent, "additive_speed")
        registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(AdditiveConsumptionLogic(acidFluidInput, 5, additiveMultiplierComponent),
                MACHINE_DEFAULT_CONSUMPTION_LOGIC_NAME)
        this.addLogicStrategy(ItemProcessingLogic(
                RecipeManager.RecipeType.CHEMICAL_ETCHING,
                inputInventory = inputInventory.inventory,
                outputInventory = outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                progress = this.progressComponent,
                baseTickEnergyCost = 120
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun getGui(player: EntityPlayer?): TCGui {

        val gui = SimpleGui(container = TCContainer(this))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()

                val te = machine as TileEntityChemicalEtchingChamber

                var xOff = 10
                var yOff = 20

                val spacer = 5
                val spacerSmall = 2

                xOff += components.addElement(DefaultEnergyMeter(xOff, yOff, te.energyStorageComponent, gui)).width
                xOff += spacer

                xOff += components.addElement(DefaultFluidMeter(xOff, 20, te.acidFluidInput, gui)).width
                xOff += spacer * 2

                val space = (50) / 2
                var slot = components.addElement(TCSlotIO(te.inputInventory.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventory.inventoryType
                xOff += slot.width + spacerSmall

                slot = components.addElement(TCSlotIO(te.inputInventory.inventory, 1, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventory.inventoryType
                xOff += slot.width + spacerSmall

                slot = components.addElement(TCSlotIO(te.inputInventory.inventory, 2, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventory.inventoryType
                xOff += slot.width + spacer

                xOff += components.addElement(DefaultProgressBar(xOff, 20 + space - 8, Orientation.RIGHT, te.progressComponent, gui)).width
                xOff += spacer

                slot = components.addElement(TCSlotIO(te.outputInventoryComponent.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.outputInventoryComponent.inventoryType

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })

        addDefaultTabs(gui, player)
        initGui(gui, player)

        return gui
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return if (inventory == this.inputInventory.inventory) {
            this.recipes.any { it.getInput()[slot].test(stack) }
        } else {
            false
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {}
}
