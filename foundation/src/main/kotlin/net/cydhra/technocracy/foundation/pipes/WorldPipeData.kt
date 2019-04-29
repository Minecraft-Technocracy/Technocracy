package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData
import net.minecraft.world.World
import java.lang.IllegalStateException


class WorldPipeData(val network: Network) : WorldSavedData("${TCFoundation.MODID}:pipe_network") {

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}