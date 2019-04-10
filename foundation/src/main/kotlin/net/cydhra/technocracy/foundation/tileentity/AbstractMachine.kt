package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.components.IComponent
import net.cydhra.technocracy.foundation.tileentity.components.MachineUpgrades
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

abstract class AbstractMachine : TileEntity(), ITickable {

    var rotation = EnumFacing.NORTH
    var redstoneMode = RedstoneMode.IGNORE

    var upgrades = MachineUpgrades()

    var components = HashSet<IComponent>()

    init {
        components.add(upgrades)
    }

    override fun readFromNBT(nbtTags: NBTTagCompound) {
        super.readFromNBT(nbtTags)

        if (nbtTags.hasKey("facing")) {
            rotation = EnumFacing.values()[(nbtTags.getInteger("facing"))]
        }
        if (nbtTags.hasKey("redstone")) {
            redstoneMode = RedstoneMode.values()[(nbtTags.getInteger("redstone"))]
        }

        for(comp in components) {
            comp.readFromNBT(nbtTags)
        }
    }

    override fun writeToNBT(nbtTags: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbtTags)

        nbtTags.setInteger("facing", rotation.ordinal)
        nbtTags.setInteger("redstone", redstoneMode.ordinal)

        for(comp in components) {
            comp.writeToNBT(nbtTags)
        }

        return nbtTags
    }

    abstract override fun update()

    enum class RedstoneMode {
        HIGH, LOW, IGNORE
    }
}