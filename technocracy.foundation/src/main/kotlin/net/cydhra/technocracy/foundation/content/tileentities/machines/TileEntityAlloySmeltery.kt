package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotPlayer
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.container.TCContainerTab
import net.cydhra.technocracy.foundation.client.gui.container.components.PlayerSlotComponent
import net.cydhra.technocracy.foundation.client.gui.container.components.SlotComponent
import net.cydhra.technocracy.foundation.client.gui.machine.BaseMachineTab
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
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
import net.cydhra.technocracy.foundation.util.readCompoundTag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityAlloySmeltery : MachineTileEntity(), TEInventoryProvider {

    /**
     * Input inventory for the pulverizer with one slot
     */
    private val inputInventoryComponent = InventoryTileEntityComponent(3, this, EnumFacing.WEST)

    /**
     * Output inventory for the pulverizer with one slot
     */
    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.THERMAL, MachineUpgradeClass.ELECTRICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent))

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.ALLOY) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.ALLOY,
                inputInventory = this.inputInventoryComponent.inventory,
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 10,
                progress = this.progressComponent), MACHINE_PROCESSING_LOGIC_NAME)
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory && this.recipes.any { recipe ->
            recipe.getInput().any { it.test(stack) }
        }
                && (0 until this.inputInventoryComponent.inventory.slots).all { index ->
            index == slot || !this.inputInventoryComponent.inventory.getStackInSlot(index).isItemEqual(stack)
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun getContainer(player: EntityPlayer?): TCContainer {
        val container = MachineContainer(this)
        val mainTab = TCContainerTab()

        for (i in 0 until this.inputInventoryComponent.inventory.size) {
            mainTab.components.add(SlotComponent(inputInventoryComponent.inventory, i, type = inputInventoryComponent.inventoryType))
        }
        mainTab.components.add(SlotComponent(outputInventoryComponent.inventory, 0, type = outputInventoryComponent.inventoryType))

        if (player != null)
            addPlayerContainerSlots(mainTab, player)

        container.registerTab(mainTab)

        addDefaultContainerTabs(container, player)

        return container
    }

    @SideOnly(Side.CLIENT)
    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = TCGui(container = getContainer(player))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()

                val te = machine as TileEntityAlloySmeltery

                var xOff = 10
                var yOff = 20

                val spacer = 5
                val spacerSmall = 2

                xOff += components.addElement(DefaultEnergyMeter(xOff, yOff, te.energyStorageComponent, gui)).width
                xOff += spacer * 4

                val space = (50) / 2
                var slot = components.addElement(TCSlotIO(te.inputInventoryComponent.inventory, 0, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventoryComponent.inventoryType
                xOff += slot.width + spacerSmall

                slot = components.addElement(TCSlotIO(te.inputInventoryComponent.inventory, 1, xOff, 20 + space - 8, gui))
                slot.type = te.inputInventoryComponent.inventoryType
                xOff += slot.width + spacerSmall

                slot = components.addElement(TCSlotIO(te.inputInventoryComponent.inventory, 2, xOff, 20 + space - 8, gui))
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

    override fun initGui(gui: TCGui, player: EntityPlayer?) {
        gui.registerTab(object : TCTab("Example", gui) {
            override fun init() {
                addComponent(DefaultLabel(10, 20, "Hello World"))
            }
        })
    }
}