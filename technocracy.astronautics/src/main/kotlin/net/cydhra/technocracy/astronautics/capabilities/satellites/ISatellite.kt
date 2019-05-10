package net.cydhra.technocracy.astronautics.capabilities.satellites

import net.minecraft.nbt.NBTTagCompound

/**
 * Interface a satellite must implement
 */
interface ISatellite {

    /**
     * Serialize the satellite data to the given NBT [compound]
     */
    fun serializeNbt(compound: NBTTagCompound)
}