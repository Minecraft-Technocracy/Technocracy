package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
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
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityIndustrialRefinery : MachineTileEntity(), TEInventoryProvider<DynamicInventoryCapability> {
    private val inputFluidComponent1 = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST))

    private val inputFluidComponent2 = TileEntityFluidComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.SOUTH))

    private val inputItemComponent = TileEntityInventoryComponent(2, this, EnumFacing.NORTH,
            DynamicInventoryCapability.InventoryType.BOTH)

    private val outputInventoryComponent = TileEntityInventoryComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    /**
     * All recipes of the industrial refinery; loaded lazily so they are not loaded before game loop, as they
     * might not have been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.INDUSTRIAL_REFINERY) ?: emptyList())
    }

    init {
        this.registerComponent(inputFluidComponent1, "fluid1")
        this.registerComponent(inputFluidComponent2, "fluid2")
        this.registerComponent(inputItemComponent, "item")
        this.registerComponent(outputInventoryComponent, "output")

        this.addLogicStrategy(ItemProcessingLogic(
                RecipeManager.RecipeType.INDUSTRIAL_REFINERY,
                inputInventory = inputItemComponent.inventory,
                outputInventory = outputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent1.fluid, inputFluidComponent2.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                progress = this.progressComponent,
                baseTickEnergyCost = 200
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = SimpleGui(container = TCContainer(this))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()
                val te = this@TileEntityIndustrialRefinery

                var xOff = 10
                var yOff = 20

                val spacer = 5
                val spacerSmall = 2

                xOff += components.addElement(DefaultEnergyMeter(xOff, yOff, te.energyStorageComponent, gui)).width
                xOff += spacer

                xOff += components.addElement(DefaultFluidMeter(xOff, 20, te.inputFluidComponent1, gui, 1)).width
                xOff += spacerSmall
                xOff += components.addElement(DefaultFluidMeter(39, 20, te.inputFluidComponent2, gui, 1)).width
                xOff += spacer * 3

                val space = (64) / 2
                var slot = components.addElement(TCSlotIO(te.inputItemComponent.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.inputItemComponent.inventoryType
                xOff += slot.width + spacerSmall

                slot = components.addElement(TCSlotIO(te.inputItemComponent.inventory, 1, xOff, 20 + space - 8, gui))
                slot.type = te.inputItemComponent.inventoryType
                components.add(slot)
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