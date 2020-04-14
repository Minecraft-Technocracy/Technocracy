package net.cydhra.technocracy.foundation.model.items.capability

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability


class ICapabilityWrapperStorage : Capability.IStorage<ICapabilityWrapperCapability> {
    //does not save or load anything as it is handled in ItemCapabilityWrapper
    override fun readNBT(capability: Capability<ICapabilityWrapperCapability>?, instance: ICapabilityWrapperCapability?, side: EnumFacing?, nbt: NBTBase?) {
    }

    override fun writeNBT(capability: Capability<ICapabilityWrapperCapability>?, instance: ICapabilityWrapperCapability?, side: EnumFacing?): NBTBase? {
        return NBTTagCompound()
    }
}