package net.cydhra.technocracy.foundation.data.general

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable


abstract class AbstractSaveDataElement(val name: String) : INBTSerializable<NBTTagCompound> {
    abstract fun reset()
}