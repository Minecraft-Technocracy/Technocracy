package net.cydhra.technocracy.foundation.data.world.api

import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.DimensionManager

/**
 * Manager for [WorldSavedData] instances used by technocracy. It stores them and saves them to NBT when necessary.
 *
 * @param name the NBT tag to identify the data managed by this particular instance
 */
class DataManager(name: String = "tc_data") : WorldSavedData(name) {

    // TODO since most of the code is inside the companion anyways, maybe refactor the manager into an object
    companion object {
        var manager: DataManager? = null
        val modules = mutableListOf<AbstractSaveDataElement>(GroupManager)

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