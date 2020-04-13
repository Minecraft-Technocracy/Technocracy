package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent
import net.minecraft.nbt.NBTTagCompound

/**
 * Component to store raw heat.
 *
 * @param initialHeat the initial heat of the component
 * @param heatCapacity the maximum heat of the component
 */
class TileEntityHeatStorageComponent(initialHeat: Int, var heatCapacity: Int = 1000) :
        AbstractTileEntityComponent() {

    override val type: ComponentType = ComponentType.OTHER

    companion object {
        const val NBT_KEY_HEAT = "heat"
        const val NBT_KEY_CAPACITY = "capacity"
    }

    var heat: Int = initialHeat

    override fun serializeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()

        compound.setInteger(NBT_KEY_HEAT, this.heat)
        compound.setInteger(NBT_KEY_CAPACITY, this.heatCapacity)

        return compound
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.heat = nbt.getInteger(NBT_KEY_HEAT)
        this.heatCapacity = nbt.getInteger(NBT_KEY_CAPACITY)
    }
}