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
     */
    fun registerComponent(component: IComponent, name: String)

    /**
     * Serialize this aggregatable into an NBT tag compound
     */
    fun serializeNBT(compound: NBTTagCompound): NBTTagCompound

    /**
     * Deserialize the given NBT compound and load the values into this aggregatable.
     */
    fun deserializeNBT(compound: NBTTagCompound)
}