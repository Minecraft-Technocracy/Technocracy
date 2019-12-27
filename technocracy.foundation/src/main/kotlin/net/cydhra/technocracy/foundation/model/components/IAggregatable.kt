package net.cydhra.technocracy.foundation.model.components

import net.minecraft.nbt.NBTTagCompound

/**
 * Common interface to all systems within the mod, that can be aggregated from components
 */
interface IAggregatable {
    /**
     * Get a list of all registered components and their nbt keys.
     */
    fun getComponents(): MutableList<Pair<String, IComponent>>

    /**
     * Register a new component within the system
     *
     * @param component component implementation
     * @param name unique name for the component that is used as NBT key. If this is not unique, no guarantees about
     * the machines behaviour and stability of the mod can be made.
     */
    fun registerComponent(component: IComponent, name: String)

    /**
     * Remove the component associated with the given name
     */
    fun removeComponent(name: String)

    /**
     * Serialize this aggregatable into an NBT tag compound
     */
    fun serializeNBT(compound: NBTTagCompound): NBTTagCompound

    /**
     * Deserialize the given NBT compound and load the values into this aggregatable.
     */
    fun deserializeNBT(compound: NBTTagCompound)
}