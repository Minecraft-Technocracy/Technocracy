package net.cydhra.technocracy.optics.api.capabilities.laser

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * (De-)Serialize all data associated with a laser emitter.
 */
class LaserEmitterStorage : Capability.IStorage<ILaserEmitter> {
    override fun readNBT(capability: Capability<ILaserEmitter>, instance: ILaserEmitter, side: EnumFacing?, nbt: NBTBase) {

    }

    override fun writeNBT(capability: Capability<ILaserEmitter>, instance: ILaserEmitter, side: EnumFacing?): NBTBase? {
        return NBTTagCompound()
    }
}