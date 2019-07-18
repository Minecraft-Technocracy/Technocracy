package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.client.gui.tabs.*
import net.cydhra.technocracy.foundation.tileentity.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.*
import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.cydhra.technocracy.foundation.tileentity.logic.LogicClientDelegate
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

open class MachineTileEntity : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate() {
    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.DOWN))

    /**
     * The machine upgrades component.
     */
    /* TODO as the possible upgrades are dependant of machine type, either split this compound into single upgrades or
        at least handle it from subclass*/
    protected val machineUpgradesComponent = MachineUpgradesComponents()

    init {
        this.registerComponent(redstoneModeComponent, "redstone_mode")
        this.registerComponent(energyStorageComponent, "energy")
        this.registerComponent(machineUpgradesComponent, "upgrades")
    }

    override fun getGui(player: EntityPlayer): TCGui {
        val gui = TCGui(player, container = MachineContainer(this))
        gui.registerTab(object : BaseMachineTab(this, gui, ResourceLocation("technocracy.foundation",
                "textures/item/silicon.png")) {
            override fun init() {
                addPlayerInventorySlots(player, 8, 84)

                var inputNearestToTheMiddle = 0
                var outputNearestToTheMiddle = parent.guiWidth // nice names
                this@MachineTileEntity.getComponents().forEach {
                    when {
                        it.second is EnergyStorageComponent -> {
                            components.add(DefaultEnergyMeter(10, 20, it.second as EnergyStorageComponent))
                            if (inputNearestToTheMiddle < 20)
                                inputNearestToTheMiddle = 20
                        }
                        it.second is FluidComponent -> {
                            val component: FluidComponent = it.second as FluidComponent
                            when {
                                component.fluid.tanktype == DynamicFluidHandler.TankType.INPUT -> {
                                    components.add(DefaultFluidMeter(25, 20, component))
                                    if (inputNearestToTheMiddle < 35)
                                        inputNearestToTheMiddle = 35
                                }
                                component.fluid.tanktype == DynamicFluidHandler.TankType.OUTPUT -> {
                                    components.add(DefaultFluidMeter(145, 20, component))
                                    if (outputNearestToTheMiddle > 145)
                                        outputNearestToTheMiddle = 145
                                }
                                component.fluid.tanktype == DynamicFluidHandler.TankType.BOTH -> {
                                    TODO("not implemented")
                                }
                            }
                        }
                        it.second is InventoryComponent -> {
                            val component: InventoryComponent = it.second as InventoryComponent
                            when {
                                it.first.contains("input") -> {
                                    for (i in 0 until component.inventory.slots) {
                                        components.add(TCSlotIO(component.inventory, i, 40 + i * 20, 40))
                                        val newX = 40 + (i + 1) * 20
                                        if (inputNearestToTheMiddle < newX)
                                            inputNearestToTheMiddle = newX
                                    }

                                }
                                it.first.contains("output") -> {
                                    for (i in component.inventory.slots - 1 downTo 0) {
                                        components.add(TCSlotIO(component.inventory, i, 125 + i * 20, 40))
                                        val newX = 125 + i * 20
                                        if (outputNearestToTheMiddle > newX)
                                            outputNearestToTheMiddle = newX
                                    }
                                }
                            }
                        }
                    }
                }

                components.add(DefaultProgressBar((outputNearestToTheMiddle - inputNearestToTheMiddle) / 2 - 11 + inputNearestToTheMiddle, 40, Orientation.RIGHT))
            }
        })
        gui.registerTab(MachineSettingsTab(gui, this, player))
        return gui
    }

    override fun update() {
        // update ILogic strategies
        this.tick()
    }
}