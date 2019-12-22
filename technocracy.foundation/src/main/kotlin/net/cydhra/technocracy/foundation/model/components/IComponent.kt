package net.cydhra.technocracy.foundation.model.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.minecraft.nbt.NBTTagCompound

/**
 * Common interface to all components that can be aggregated to form complex stateful systems, like (tile)-entities.
 * Every class that is [aggregatable][IAggregatable] can be composed of multiple [IComponent]s. This way simple
 * behaviour can be abstracted and then combined into complex compositions.
 */
interface IComponent {
    val type: ComponentType

    /**
     * Write the component's state to an NBT value
     */
    fun serializeNBT(): NBTTagCompound

    /**
     * deserialize and apply the component's state from the given tag
     */
    fun deserializeNBT(nbt: NBTTagCompound)

    /**
     * called while the component is getting registered
     */
    fun onRegister()
}