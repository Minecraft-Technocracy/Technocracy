package net.cydhra.technocracy.foundation.model.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.container.TCContainerTab
import net.cydhra.technocracy.foundation.client.gui.container.components.ClickableComponent
import net.cydhra.technocracy.foundation.client.gui.container.components.PlayerSlotComponent
import net.cydhra.technocracy.foundation.client.gui.container.components.SlotComponent
import net.cydhra.technocracy.foundation.client.gui.machine.BaseMachineTab
import net.cydhra.technocracy.foundation.client.gui.machine.MachineSettingsTab
import net.cydhra.technocracy.foundation.client.gui.machine.MachineUpgradesTab
import net.cydhra.technocracy.foundation.client.gui.machine.SideConfigTab
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.*
import net.cydhra.technocracy.foundation.content.tileentities.logic.RedstoneLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_SPEED
import net.cydhra.technocracy.foundation.model.tileentities.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogicClient
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing

open class MachineTileEntity : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate() {

    companion object {
        const val MACHINE_PROCESSING_LOGIC_NAME = "default_processing"
        const val MACHINE_DEFAULT_CONSUMPTION_LOGIC_NAME = "default_consumption"
        const val MACHINE_REDSTONE_LOGIC_NAME = "default_redstone"
    }

    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeTileEntityComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageTileEntityComponent(mutableSetOf(EnumFacing.DOWN))

    protected val progressComponent = ProgressTileEntityComponent()

    protected val processingSpeedComponent = MultiplierTileEntityComponent(MACHINE_UPGRADE_SPEED)

    protected val energyCostComponent = MultiplierTileEntityComponent(MACHINE_UPGRADE_ENERGY)

    init {
        this.registerComponent(redstoneModeComponent, "redstone_mode")
        this.registerComponent(energyStorageComponent, "energy")
        this.registerComponent(progressComponent, "progress")
        this.registerComponent(processingSpeedComponent, "processing_speed")
        this.registerComponent(energyCostComponent, "processing_cost")
    }

    override fun onLoad() {
        super.onLoad()

        this.addLogicStrategy(RedstoneLogic(this.world, this.pos, this.redstoneModeComponent),
                MACHINE_REDSTONE_LOGIC_NAME)
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = TCGui(container = getContainer(player))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()
                var nextOutput = 125
                var nextInput = 10
                var inputNearestToTheMiddle = 0
                var outputNearestToTheMiddle = parent.guiWidth
                var foundProgressComponent: ProgressTileEntityComponent? = null
                val sortedComponents = listOf(*this@MachineTileEntity.getComponents().toTypedArray())
                        .sortedBy { (_, component) -> component !is FluidTileEntityComponent }
                        .sortedBy { (_, component) -> component !is EnergyStorageTileEntityComponent }
                sortedComponents.forEach { (name, component) ->
                    when (component) {
                        is EnergyStorageTileEntityComponent -> {
                            components.add(DefaultEnergyMeter(nextInput, 20, component, gui))
                            if (inputNearestToTheMiddle < 20) {
                                inputNearestToTheMiddle = 20
                                nextInput = 25
                            }
                        }
                        is FluidTileEntityComponent -> {
                            when {
                                component.fluid.tanktype == DynamicFluidCapability.TankType.INPUT -> {
                                    components.add(DefaultFluidMeter(nextInput, 20, component, gui))
                                    if (inputNearestToTheMiddle < nextInput - 5) {
                                        inputNearestToTheMiddle = nextInput - 5 // 5 is the space between components
                                    }
                                    nextInput += 25 // fluid meter width (10) + space (5)
                                }
                                component.fluid.tanktype == DynamicFluidCapability.TankType.OUTPUT -> {
                                    components.add(DefaultFluidMeter(nextOutput, 20, component, gui))
                                    if (outputNearestToTheMiddle > nextOutput)
                                        outputNearestToTheMiddle = nextOutput
                                    nextOutput += 25
                                }
                                component.fluid.tanktype == DynamicFluidCapability.TankType.BOTH -> {
                                    TODO("not implemented")
                                }
                            }
                        }
                        is InventoryTileEntityComponent -> {
                            if (component.inventoryType != DynamicInventoryCapability.InventoryType.OUTPUT) {
                                for (i in 0 until component.inventory.slots) {
                                    if (nextInput == 25)
                                        nextInput = 30
                                    val slot = TCSlotIO(component.inventory, i, nextInput, 40, gui)
                                    slot.type = component.inventoryType
                                    components.add(slot)
                                    if (inputNearestToTheMiddle < nextInput)
                                        inputNearestToTheMiddle = nextInput
                                    nextInput += 20
                                }

                            } else {
                                for (i in component.inventory.slots - 1 downTo 0) {
                                    val slot = TCSlotIO(component.inventory, i, 125 + i * 20, 40, gui)
                                    slot.type = component.inventoryType
                                    components.add(slot)
                                    val newX = 125 + i * 20
                                    if (outputNearestToTheMiddle > newX)
                                        outputNearestToTheMiddle = newX
                                }
                            }
                        }
                        is ProgressTileEntityComponent -> {
                            foundProgressComponent = component
                        }
                    }
                }
                if (foundProgressComponent != null)
                    components.add(DefaultProgressBar((outputNearestToTheMiddle - inputNearestToTheMiddle) / 2 + inputNearestToTheMiddle,
                            40,
                            Orientation.RIGHT,
                            foundProgressComponent as ProgressTileEntityComponent,
                            gui))

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })

        addDefaultTabs(gui, player)
        initGui(gui, player)

        return gui
    }


    override fun getContainer(player: EntityPlayer?): TCContainer {
        val container = TCContainer()
        val mainTab = TCContainerTab()

        getComponents().filter { it.second is InventoryTileEntityComponent }.forEach { (_, com) ->
            val component = com as InventoryTileEntityComponent
            if (component.inventoryType != DynamicInventoryCapability.InventoryType.OUTPUT) {
                for (i in 0 until component.inventory.slots) {
                    mainTab.components.add(SlotComponent(component.inventory, i, component.inventoryType))
                }
            } else {
                for (i in component.inventory.slots - 1 downTo 0) {
                    mainTab.components.add(SlotComponent(component.inventory, i, component.inventoryType))
                }
            }
        }

        if (player != null)
            addPlayerContainerSlots(mainTab, player)

        container.registerTab(mainTab)

        addDefaultContainerTabs(container, player)
        return container
    }

    open fun addDefaultContainerTabs(container: TCContainer, player: EntityPlayer?) {
        //MachineSettingsTab
        val redstoneSettings = TCContainerTab()
        var index = 0
        getComponents().forEach {
            if (it.second is RedstoneModeTileEntityComponent) {

                val click = ClickableComponent(index++) { player, tileEntity, button ->
                    if (button == 0) {
                        val component = it.second as RedstoneModeTileEntityComponent
                        component.redstoneMode = RedstoneModeTileEntityComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeTileEntityComponent.RedstoneMode.values().size]
                    }
                }
                redstoneSettings.components.add(click)
            }
        }
        container.registerTab(redstoneSettings)

        val upgradesComponent = this.getComponents().firstOrNull { (_, c) -> c is MachineUpgradesTileEntityComponent }?.second
        if (upgradesComponent != null) {

            val upgradeTab = TCContainerTab()
            if (player != null)
                addPlayerContainerSlots(upgradeTab, player)

            val upgrades = upgradesComponent as MachineUpgradesTileEntityComponent

            for (i in 0 until upgrades.numberOfUpgradeSlots) {
                upgradeTab.components.add(SlotComponent(upgrades.inventory, i, type = upgrades.inventory.slotTypes[i]
                        ?: DynamicInventoryCapability.InventoryType.BOTH))
            }
            container.registerTab(upgradeTab)
        }

        //sideconfig
        container.registerTab(TCContainerTab())
    }

    fun addPlayerContainerSlots(tab: TCContainerTab, player: EntityPlayer) {
        for (row in 0..2) {
            for (slot in 0..8) {
                tab.components.add(PlayerSlotComponent(player.inventory, slot + row * 9 + 9))
            }
        }

        for (k in 0..8) {
            tab.components.add(PlayerSlotComponent(player.inventory, k))
        }
    }

    /**
     * addes default tabs
     */
    open fun addDefaultTabs(gui: TCGui, player: EntityPlayer?) {
        gui.registerTab(MachineSettingsTab(gui, this))

        val upgradesComponent = this.getComponents().firstOrNull { (_, c) -> c is MachineUpgradesTileEntityComponent }?.second
        if (upgradesComponent != null) {
            gui.registerTab(MachineUpgradesTab(gui, upgradesComponent as MachineUpgradesTileEntityComponent, player))
        }

        gui.registerTab(SideConfigTab(gui, this, gui.tabs[0]))
    }

    open fun initGui(gui: TCGui, player: EntityPlayer?) {
    }

    override fun update() {
        // update ILogic strategies, but only server side
        if (!world.isRemote)
            this.tick()
    }
}