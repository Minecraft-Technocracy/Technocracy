package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.client.gui.machine.Tab
import net.cydhra.technocracy.foundation.client.gui.machine.tabs.WipTab
import net.cydhra.technocracy.foundation.tileentity.components.*
import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.cydhra.technocracy.foundation.tileentity.logic.LogicClientDelegate
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability

/**
 * Base class for all machine [TileEntities][TileEntity]. The tile entity automatically has capabilities for energy
 * storage and a component that defines reactions to redstone signales. Note, that the component does not handle the
 * defined reactions itself.
 */
abstract class AbstractMachine : AbstractComponentTileEntity(), ITickable, ILogicClient by LogicClientDelegate() {

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

    fun getGuiTabs(): Array<Tab> {
        return arrayOf(WipTab(500, 500))
    }

    override fun update() {
        // update ILogic strategies
        this.tick()
    }


}