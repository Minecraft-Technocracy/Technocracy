package net.cydhra.technocracy.foundation.model.tileentities.machines

import net.cydhra.technocracy.foundation.api.ecs.logic.EmptyLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicClient
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.api.tileentities.TCMachineTileEntity
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_SPEED
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.heatmeter.DefaultHeatMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.machine.BaseMachineTab
import net.cydhra.technocracy.foundation.client.gui.machine.MachineSettingsTab
import net.cydhra.technocracy.foundation.client.gui.machine.MachineUpgradesTab
import net.cydhra.technocracy.foundation.client.gui.machine.SideConfigTab
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.*
import net.cydhra.technocracy.foundation.content.tileentities.logic.RedstoneLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.CoolingUpgrade
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.LubricantUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing

open class MachineTileEntity(
    upgradeSlots: Int = 3
) : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient<ILogicParameters> by LogicClientDelegate() {

    companion object {
        const val MACHINE_PROCESSING_LOGIC_NAME = "default_processing"
        const val MACHINE_DEFAULT_CONSUMPTION_LOGIC_NAME = "default_consumption"
        const val MACHINE_REDSTONE_LOGIC_NAME = "default_redstone"
    }

    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = TileEntityRedstoneModeComponent()

    /**
     * The machines internal energy stor5age and transfer limit state
     */
    protected val energyStorageComponent = TileEntityEnergyStorageComponent(mutableSetOf(EnumFacing.DOWN))

    protected val progressComponent = TileEntityProgressComponent()

    protected val processingSpeedComponent = TileEntityMultiplierComponent(UPGRADE_SPEED)

    protected val energyCostComponent = TileEntityMultiplierComponent(UPGRADE_ENERGY)

    private val upgradesComponent = TileEntityMachineUpgradesComponent(upgradeSlots)

    /**
     * A map of all upgradable parameters within the machine. This is important because an
     * [net.cydhra.technocracy.foundation.api.upgrades.Upgradable] must expose all parameters it supports.
     */
    protected val upgradeParameters = mutableMapOf(
        UPGRADE_SPEED to processingSpeedComponent,
        UPGRADE_ENERGY to energyCostComponent
    )

    init {
        this.registerComponent(redstoneModeComponent, "redstone_mode")
        this.registerComponent(energyStorageComponent, "energy")
        this.registerComponent(progressComponent, "progress")
        this.registerComponent(processingSpeedComponent, "processing_speed")
        this.registerComponent(energyCostComponent, "processing_cost")
        this.registerComponent(upgradesComponent, "upgrades")
    }

    override fun onLoad() {
        super.onLoad()

        this.addLogicStrategy(
            RedstoneLogic(this.world, this.pos, this.redstoneModeComponent),
            MACHINE_REDSTONE_LOGIC_NAME
        )
    }

    override fun getGui(player: EntityPlayer?, other: TCGui?): TCGui {
        val gui = other ?: SimpleGui(container = TCContainer(this))
        gui.registerTab(object : BaseMachineTab(this, gui) {
            override fun init() {
                super.init()
                var nextOutput = 125
                var nextInput = 10
                var inputNearestToTheMiddle = 0
                var outputNearestToTheMiddle = parent.guiWidth
                var foundProgressComponent: TileEntityProgressComponent? = null

                val sortedComponents = listOf(*this@MachineTileEntity.getComponents().toTypedArray())
                    .sortedBy { (_, component) -> component !is TileEntityFluidComponent }
                    .sortedBy { (_, component) -> component !is TileEntityHeatStorageComponent }
                    .sortedBy { (_, component) -> component !is TileEntityEnergyStorageComponent }
                sortedComponents.forEach { (name, component) ->
                    if (name != CoolingUpgrade.COOLER_FLUID_INPUT_NAME && name != CoolingUpgrade.COOLER_FLUID_OUTPUT_NAME && name != LubricantUpgrade.LUBRICANT_FLUID_COMPONENT_NAME) {
                        when (component) {
                            is TileEntityEnergyStorageComponent -> {
                                components.add(DefaultEnergyMeter(nextInput, 20, component, gui))
                                if (inputNearestToTheMiddle < 20) {
                                    inputNearestToTheMiddle = 20
                                    nextInput = 25
                                }
                            }
                            is TileEntityFluidComponent -> {
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
                            is TileEntityHeatStorageComponent -> {
                                components.add(DefaultHeatMeter(nextInput, 20, component, gui))
                                if (inputNearestToTheMiddle < nextInput - 5) {
                                    inputNearestToTheMiddle = nextInput - 5 // 5 is the space between components
                                }
                                nextInput += 17 + 5
                            }
                            is TileEntityInventoryComponent -> {
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
                            is TileEntityProgressComponent -> {
                                foundProgressComponent = component
                            }
                        }
                    }
                }
                if (foundProgressComponent != null)
                    components.add(
                        DefaultProgressBar(
                            (outputNearestToTheMiddle - inputNearestToTheMiddle) / 2 + inputNearestToTheMiddle,
                            40,
                            Orientation.RIGHT,
                            foundProgressComponent as TileEntityProgressComponent,
                            gui
                        )
                    )

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })

        addDefaultTabs(gui, player)
        initGui(gui, player)

        return gui
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null) return true
        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16
    }

    /**
     * addes default tabs
     */
    open fun addDefaultTabs(gui: TCGui, player: EntityPlayer?) {
        gui.registerTab(MachineSettingsTab(gui, this))

        val upgradesComponent =
            this.getComponents().firstOrNull { (_, c) -> c is TileEntityMachineUpgradesComponent }?.second
        if (upgradesComponent != null) {
            gui.registerTab(MachineUpgradesTab(gui, upgradesComponent as TileEntityMachineUpgradesComponent, player))
        }

        gui.registerTab(SideConfigTab(gui, this, gui.getTab(0)))
    }

    open fun initGui(gui: TCGui, player: EntityPlayer?) {
    }

    override fun update() {
        // update ILogic strategies, but only server side
        if (!world.isRemote)
            this.tick(EmptyLogicParameters)
    }

    /**
     * Register a new upgradable parameter at the machine
     *
     * @param parameter the [UpgradeParameter] that shall be registered as supported
     * @param multiplierComponent the multiplier affected by the parameter
     */
    protected fun registerUpgradeParameter(
        parameter: UpgradeParameter,
        multiplierComponent: TileEntityMultiplierComponent
    ) {
        this.upgradeParameters[parameter] = multiplierComponent
    }

    override fun supportsParameter(parameter: UpgradeParameter): Boolean {
        return parameter == UPGRADE_GENERIC || this.upgradeParameters.keys.contains(parameter)
    }

    /**
     * Apply a modification to a parameter. If this machine does not support the parameter or the parameter is
     * [UPGRADE_GENERIC], a [NullPointerException] will be thrown
     */
    override fun upgradeParameter(parameter: UpgradeParameter, modification: Double) {
        this.upgradeParameters[parameter]!!.multiplier += modification
    }
}