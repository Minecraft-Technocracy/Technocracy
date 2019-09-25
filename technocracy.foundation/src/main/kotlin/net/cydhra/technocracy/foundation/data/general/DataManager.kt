package net.cydhra.technocracy.foundation.data.general

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.DimensionManager


class DataManager : WorldSavedData("TC_Data") {

    companion object {
        var manager: DataManager? = null
        val modules = mutableListOf<AbstractSaveDataElement>()

        fun init() {
            val worldServer = DimensionManager.getWorld(0)
            manager = worldServer.loadData(DataManager::class.java, "TCSaveData") as DataManager
            if (manager == null) {
                for (module in modules) {
                    module.reset()
                }

                with(DataManager()) {
                    manager = this
                    worldServer.setData("TCSaveData", this)
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