package net.cydhra.technocracy.foundation.data.general

import net.cydhra.technocracy.foundation.data.OwnershipManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.DimensionManager


class DataManager : WorldSavedData("tc_data") {

    companion object {
        var manager: DataManager? = null
        val modules = mutableListOf<AbstractSaveDataElement>(OwnershipManager)

        fun init() {
            val worldServer = DimensionManager.getWorld(0)
            manager = worldServer.loadData(DataManager::class.java, "tc_data") as? DataManager
            if (manager == null) {
                for (module in modules) {
                    module.reset()
                }

                with(DataManager()) {
                    manager = this
                    worldServer.setData("tc_data", this)
                    markDirty()
                }
            }
        }
    }


    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        for (module in modules) {
            compound.setTag(module.name, module.serializeNBT())
        }
        return compound
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        for (module in modules) {
            module.reset()
            if (nbt.hasKey(module.name))
                module.deserializeNBT(nbt.getCompoundTag(module.name))
        }
    }
}