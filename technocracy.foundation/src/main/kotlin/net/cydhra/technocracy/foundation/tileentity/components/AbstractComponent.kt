package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.tileentity.AbstractTileEntity
import net.minecraft.nbt.NBTTagCompound

/**
 * Class defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
abstract class AbstractComponent {

    lateinit var tile: AbstractTileEntity

    abstract val type: ComponentType

    /**
     * Write the component's state to an NBT value
     */
    abstract fun serializeNBT(): NBTTagCompound

    /**
     * deserialize and apply the component's state from the given tag
     */
    abstract fun deserializeNBT(nbt: NBTTagCompound)

}

enum class ComponentType(val supportsWaila: Boolean = false) {
    ENERGY(true),
    FLUID(true),
    INVENTORY(true),
    FACADE,
    PIPE_TYPES(true),
    HEAT(true),
    UPGRADES(true),
    NETWORK,
    REDSTONE_MODE(true),
    PROGRESS(true)
}