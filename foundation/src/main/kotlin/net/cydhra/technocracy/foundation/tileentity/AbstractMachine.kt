package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorageStategy
import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.tileentity.components.IComponent
import net.cydhra.technocracy.foundation.tileentity.components.MachineUpgrades
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability

abstract class AbstractMachine(private val energyStorage: DynamicEnergyStorage) : TileEntity(), ITickable {

    protected var rotation = EnumFacing.NORTH
    protected var redstoneMode = RedstoneMode.IGNORE

    protected val upgrades = MachineUpgrades()

    protected val components = HashSet<IComponent>()

    init {
        components.add(upgrades)
        TileEntity.register("${TCFoundation.MODID}:${javaClass.simpleName}", this.javaClass)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("facing")) {
            rotation = EnumFacing.values()[(compound.getInteger("facing"))]
        }
        if (compound.hasKey("redstone")) {
            redstoneMode = RedstoneMode.values()[(compound.getInteger("redstone"))]
        }

        if (compound.hasKey("energy")) {
            DynamicEnergyStorageStategy.readNBT(this.energyStorage, compound.getCompoundTag("energy"))
        }

        for (comp in components) {
            comp.readFromNBT(compound)
        }

    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)

        compound.setInteger("facing", rotation.ordinal)
        compound.setInteger("redstone", redstoneMode.ordinal)
        compound.setTag("energy", DynamicEnergyStorageStategy.writeNBT(this.energyStorage))

        for (comp in components) {
            comp.writeToNBT(compound)
        }

        return compound
    }

    abstract override fun update()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (capability == EnergyCapabilityProvider.CAPABILITY_ENERGY)
            true
        else
            super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == EnergyCapabilityProvider.CAPABILITY_ENERGY)
            EnergyCapabilityProvider.CAPABILITY_ENERGY!!.cast<T>(this.energyStorage)
        else
            super.getCapability(capability, facing)
    }

    enum class RedstoneMode {
        HIGH, LOW, IGNORE
    }
}