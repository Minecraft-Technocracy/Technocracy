package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTBase

/**
 * Interface defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
interface IComponent {

    val type: ComponentType

    /**
     * Write the component's state to an NBT value
     */
    fun serializeNBT(): NBTBase

    /**
     * deserialize and apply the component's state from the given tag
     */
    fun deserializeNBT(nbt: NBTBase)

}

enum class ComponentType(val wailaSupport: Boolean = false) {
    ENERGY(true),
    FLUID(true),
    INVENTORY,
    FACADE,
    PIPE_TYPES(true),
    HEAT(true),
    UPGRADES(true),
    NETWORK,
    REDSTONE_MODE(true)
}