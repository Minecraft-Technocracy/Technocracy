package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 * Class defining a common component of machine tile entity implementations. All components define some ability
 * of a machine that requires saved state.
 */
abstract class AbstractComponent {

    lateinit var tile: TileEntity

    var syncToClient = false

    fun markDirty(needsClientRerender: Boolean = false) {
        if (syncToClient && needsClientRerender) {
            tile.world.notifyBlockUpdate(tile.pos, tile.world.getBlockState(tile.pos), tile.world.getBlockState(tile.pos), 0)
        }
        tile.markDirty()
    }

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