package net.cydhra.technocracy.foundation.content.items.capability

import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.items.capability.ICapabilityWrapperCapability
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound


class DefaultItemCapability : ICapabilityWrapperCapability {

    val components = mutableMapOf<String, AbstractItemComponent>()

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        return player?.isEntityAlive ?: true
    }

    override fun getComponents(): List<Pair<String, IComponent>> {
        return components.toList()
    }

    override fun registerComponent(component: IComponent, name: String) {
        components[name] = component as AbstractItemComponent
    }

    override fun removeComponent(name: String) {
        components.remove(name)
    }

    override fun serializeNBT(compound: NBTTagCompound): NBTTagCompound {
        return getCombinedNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt != null) {
            components.forEach {
                it.value.deserializeNBT(nbt.getCompoundTag(it.key))
            }
        }
    }

    fun getCombinedNBT(): NBTTagCompound {
        val wrapped = NBTTagCompound()
        for ((k, v) in components) {
            wrapped.setTag(k, v.serializeNBT())
        }
        return wrapped
    }
}