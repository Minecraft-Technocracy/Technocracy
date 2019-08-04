package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

/**
 * Component to store raw heat.
 *
 * @param initialHeat the initial heat of the component
 * @param heatCapacity the maximum heat of the component
 * @param drainEfficiency a multiplier between 0 and 1 on how much heat is lost on draining heat from the component
 */
class HeatStorageComponent(initialHeat: Int, var heatCapacity: Int = 1000, var drainEfficiency: Float = 1.0f) :
        AbstractComponent() {

    override val type: ComponentType = ComponentType.HEAT

    companion object {
        const val NBT_KEY_HEAT = "heat"
        const val NBT_KEY_CAPACITY = "capacity"
        const val NBT_KEY_EFFICIENCY = "efficiency"
    }

    var heat: Int = initialHeat

    override fun serializeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()

        compound.setInteger(NBT_KEY_HEAT, this.heat)
        compound.setInteger(NBT_KEY_CAPACITY, this.heatCapacity)
        compound.setFloat(NBT_KEY_EFFICIENCY, this.drainEfficiency)

        return compound
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.heat = nbt.getInteger(NBT_KEY_HEAT)
        this.heatCapacity = nbt.getInteger(NBT_KEY_CAPACITY)
        this.drainEfficiency = nbt.getFloat(NBT_KEY_EFFICIENCY)
    }
}