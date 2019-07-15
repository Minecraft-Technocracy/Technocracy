package net.cydhra.technocracy.astronautics.capabilities.satellites

import net.minecraft.nbt.NBTTagCompound

/**
 * Interface a satellite must implement
 */
interface ISatellite {

    /**
     * Satellite type. Must be unique for a each class and must not change for single instances
     */
    val type: String

    /**
     * Serialize the satellite data to the given NBT [compound]
     */
    fun serializeNbt(compound: NBTTagCompound)

    /**
     * Deserialize data from given compound into this instance
     */
    fun deserializeNbt(element: NBTTagCompound)

    /**
     * Tick the satellite for work it may have to do
     */
    fun tick()
}