package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.container.TCContainerTab
import net.cydhra.technocracy.foundation.client.gui.container.components.SlotComponent
import net.cydhra.technocracy.foundation.client.gui.machine.BaseMachineTab
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
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
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityChemicalProcessingChamber : MachineTileEntity(), TEInventoryProvider {

    private val inputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.WEST)
    private val inputFluidComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))
    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent))

    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.CHEMICAL_PROCESSING) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputInventoryComponent, "output_inventory")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.CHEMICAL_PROCESSING,
                inputInventory = this.inputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                outputInventory = outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 60,
                progress = this.progressComponent
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun getContainer(player: EntityPlayer?): TCContainer {
        val container = MachineContainer(this)
        val mainTab = TCContainerTab()

        for (i in 0 until this.inputInventoryComponent.inventory.size) {
            mainTab.components.add(SlotComponent(inputInventoryComponent.inventory, i, type = inputInventoryComponent.inventoryType))
        }
        for (i in 0 until this.outputInventoryComponent.inventory.size) {
            mainTab.components.add(SlotComponent(outputInventoryComponent.inventory, i, type = outputInventoryComponent.inventoryType))
        }

        if (player != null)
            addPlayerContainerSlots(mainTab, player)

        container.registerTab(mainTab)

        addDefaultContainerTabs(container, player)

        return container
    }

    override fun getGui(player: EntityPlayer?): TCGui {

        val gui = TCGui(container = getContainer(player))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()

                val te = machine as TileEntityChemicalProcessingChamber

                var xOff = 10
                var yOff = 20

                val spacer = 5
                val spacerSmall = 2

                xOff += components.addElement(DefaultEnergyMeter(xOff, yOff, te.energyStorageComponent, gui)).width
                xOff += spacer

                xOff += components.addElement(DefaultFluidMeter(xOff, yOff, te.inputFluidComponent, gui)).width
                xOff += spacer * 6

                val space = (50) / 2
                var slot = components.addElement(TCSlotIO(te.inputInventoryComponent.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventoryComponent.inventoryType
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
        return inventory == inputInventoryComponent.inventory && this.recipes.any { recipe ->
            recipe.getInput().any { it.test(stack) }
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }
}