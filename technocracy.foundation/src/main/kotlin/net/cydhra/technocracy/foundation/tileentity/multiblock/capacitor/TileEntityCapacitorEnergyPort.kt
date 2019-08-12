package net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor

import net.cydhra.technocracy.foundation.multiblock.CapacitorMultiBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage

class TileEntityCapacitorEnergyPort : TileEntityMultiBlockPart<CapacitorMultiBlock>(CapacitorMultiBlock::class,
        ::CapacitorMultiBlock) {

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
            capability == CapabilityEnergy.ENERGY

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityEnergy.ENERGY.cast<T>(
                    multiblockController?.controllerTileEntity?.energyStorageComponent?.energyStorage)
                    ?: return EnergyStorage(1) as T
        else
            null
    }
}