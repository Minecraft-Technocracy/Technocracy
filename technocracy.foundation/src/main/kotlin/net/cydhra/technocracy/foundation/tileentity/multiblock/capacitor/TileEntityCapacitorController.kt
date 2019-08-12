package net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor

import net.cydhra.technocracy.foundation.multiblock.CapacitorMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

class TileEntityCapacitorController : TileEntityMultiBlockPart<CapacitorMultiBlock>(CapacitorMultiBlock::class,
        ::CapacitorMultiBlock), ITileEntityMultiblockController {

    val energyStorageComponent = EnergyStorageComponent(EnumFacing.values().toMutableSet())

    init {
        energyStorageComponent.energyStorage.capacity = 100000
        this.registerComponent(energyStorageComponent, "storage")
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        return this.serializeNBT(super.writeToNBT(data))
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        this.deserializeNBT(data)
    }
}