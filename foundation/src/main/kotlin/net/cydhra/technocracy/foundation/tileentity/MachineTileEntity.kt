package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.client.gui.machine.Tab
import net.cydhra.technocracy.foundation.client.gui.machine.tabs.WipTab
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.components.MachineUpgradesComponents
import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent
import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.cydhra.technocracy.foundation.tileentity.logic.LogicClientDelegate
import net.minecraft.util.EnumFacing

open class MachineTileEntity : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate() {
    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.UP))

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

    override fun getAvailableGUITabs(): Array<Tab> {
        return arrayOf(WipTab(500, 500))
    }

    override fun update() {
        // update ILogic strategies
        this.tick()
    }
}