package net.cydhra.technocracy.foundation.items.capability

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable


class ItemCapabilityWrapper(val stack: ItemStack, val capabilities: Map<String, AbstractItemCapabilityComponent>) : ICapabilitySerializable<NBTTagCompound> {
    init {
        capabilities.forEach {
            it.value.wrapper = this
        }
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val option = capabilities.values.stream().filter { it.hasCapability(capability, facing) }.findFirst()
        if (option.isPresent)
            return option.get().getCapability(capability, facing)

        return null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        for (cap in capabilities.values) {
            if (cap.hasCapability(capability, facing))
                return true
        }
        return false
    }

    fun getCombinedNBT(): NBTTagCompound {
        val wrapped = NBTTagCompound()
        for ((k, v) in capabilities) {
            wrapped.setTag(k, v.serializeNBT())
        }
        return wrapped
    }

    fun updateItemStack() {
        /*if (!stack.hasTagCompound())
            stack.tagCompound = NBTTagCompound()
        stack.tagCompound!!.setTag("capabilities", getCombinedNBT())*/
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt != null) {
            capabilities.forEach {
                it.value.deserializeNBT(nbt.getCompoundTag(it.key))
            }
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return getCombinedNBT()
    }
}