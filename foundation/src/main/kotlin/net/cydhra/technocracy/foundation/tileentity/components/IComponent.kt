package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

/**
 * Interface defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
interface IComponent {

    /**
     * Write the component's state to the given [NBTTagCompound]
     */
    fun writeToNBT(compound: NBTTagCompound)

    /**
     * Read the component's state from the given [NBTTagCompound]
     */
    fun readFromNBT(compound: NBTTagCompound)

}