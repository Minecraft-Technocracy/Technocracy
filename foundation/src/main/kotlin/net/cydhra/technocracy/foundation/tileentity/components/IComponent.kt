package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound


interface IComponent {

    fun readFromNBT(nbtTags: NBTTagCompound)
    fun writeToNBT(nbtTags: NBTTagCompound)

}