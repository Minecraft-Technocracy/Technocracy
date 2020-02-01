package net.cydhra.technocracy.optics.api.capabilities.laser

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * (De-)Serialize all data associated with a laser absorber.
 */
class LaserAbsorberStorage : Capability.IStorage<ILaserAbsorber> {
    override fun readNBT(capability: Capability<ILaserAbsorber>, instance: ILaserAbsorber, side: EnumFacing?, nbt: NBTBase) {

    }

    override fun writeNBT(capability: Capability<ILaserAbsorber>, instance: ILaserAbsorber, side: EnumFacing?): NBTBase? {
        return NBTTagCompound()
    }
}