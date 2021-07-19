package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.machine.BaseMachineTab
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityCentrifuge : MachineTileEntity(), TEInventoryProvider<DynamicInventoryCapability> {

    /**
     * Input inventory for the pulverizer with one slot
     */
    private val inputInventoryComponent =
        TileEntityInventoryComponent(1, this, EnumFacing.WEST, DynamicInventoryCapability.InventoryType.INPUT)

    /**
     * Output inventory for the pulverizer with one slot
     */
    private val outputInventoryComponent = TileEntityInventoryComponent(2, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.CENTRIFUGE) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
            recipeType = RecipeManager.RecipeType.CENTRIFUGE,
            inputInventory = this.inputInventoryComponent.inventory,
            outputInventory = this.outputInventoryComponent.inventory,
            energyStorage = this.energyStorageComponent.energyStorage,
            processSpeedComponent = this.processingSpeedComponent,
            energyCostComponent = this.energyCostComponent,
            baseTickEnergyCost = 40,
            progress = this.progressComponent
        ), MACHINE_PROCESSING_LOGIC_NAME
        )
    }

    override fun getGui(player: EntityPlayer?, other: TCGui?): TCGui {
        val gui = other ?: SimpleGui(container = TCContainer(this))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()

                val te = machine as TileEntityCentrifuge

                var xOff = 10
                var yOff = 20

                val spacer = 5
                val spacerSmall = 2

                xOff += components.addElement(DefaultEnergyMeter(xOff, yOff, te.energyStorageComponent, gui)).width
                xOff += spacer * 6

                val space = (50) / 2
                var slot = components.addElement(TCSlotIO(te.inputInventoryComponent.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventoryComponent.inventoryType
                xOff += slot.width + spacer

                xOff += components.addElement(DefaultProgressBar(xOff, 20 + space - 8, Orientation.RIGHT, te.progressComponent, gui)).width
                xOff += spacer


                slot = components.addElement(TCSlotIO(te.outputInventoryComponent.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.outputInventoryComponent.inventoryType
                xOff += slot.width + spacerSmall
                slot = components.addElement(TCSlotIO(te.outputInventoryComponent.inventory, 1, xOff, 20 + space - 8, gui))
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
        return inventory == inputInventoryComponent.inventory && this.recipes.any { it.getInput()[0].test(stack) }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }
}