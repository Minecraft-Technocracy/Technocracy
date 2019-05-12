package net.cydhra.technocracy.foundation.tileentity.api

import net.cydhra.technocracy.foundation.tileentity.components.IComponent
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Aggregation of [IComponent] implementations used for tile entities. Note, that this interface is not necessarily
 * implemented by a tile entity, but can be implemented as a delegate instead.
 */
interface TCAggregatable {
    fun getComponents(): MutableList<Pair<String, IComponent>>

    fun registerComponent(component: IComponent, name: String)

    fun serializeNBT(compound: NBTTagCompound): NBTTagCompound

    fun deserializeNBT(compound: NBTTagCompound)

    fun supportsCapability(capability: Capability<*>, facing: EnumFacing?): Boolean

    fun <T : Any?> castCapability(capability: Capability<T>, facing: EnumFacing?): T?
}