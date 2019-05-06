package net.cydhra.technocracy.foundation.tileentity.multiblock.boiler

import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing

/**
 * The tile entity for the controller block of a boiler multi-block structure
 */
class TileEntityBoilerHeater
    : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock),
        TCAggregatableTileEntity by AggregatableTileEntity() {

    companion object {
        // TODO balancing, config, upgrades, calculation etc
        const val ENERGY_COST = 100
    }

    private val energyStorageComponent: EnergyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.DOWN))

    init {
        this.registerComponent(energyStorageComponent, "energy")
    }

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    /**
     * Called by the multiblock structure to use energy.
     *
     * @return true if a sufficient amount of energy has been drawn to successfully heat water
     */
    fun tryHeating(): Boolean {
        return this.energyStorageComponent.energyStorage.consumeEnergy(ENERGY_COST)
    }
}