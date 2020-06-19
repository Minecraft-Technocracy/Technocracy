package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability


class ItemOptionalAttachedComponent<T : AbstractItemComponent>(val innerComponent: T) : AbstractItemCapabilityComponent() {

    init {
        needsClientSyncing = true
    }

    var isAttached = false
    set(value) {
        field = value
        markDirty(true)
    }
    override val type: ComponentType = ComponentType.OPTIONAL

    override fun serializeNBT(): NBTTagCompound {
        val comp = NBTTagCompound()
        comp.setBoolean("isAttached", isAttached)
        if (isAttached)
            comp.setTag("inner", innerComponent.serializeNBT())
        return comp
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("isAttached") && nbt.getBoolean("isAttached")) {
            isAttached = true
            innerComponent.deserializeNBT(nbt.getCompoundTag("inner"))
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return isAttached && innerComponent is AbstractItemCapabilityComponent &&
                innerComponent.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (!isAttached || innerComponent !is AbstractItemCapabilityComponent) {
            return null
        }

        return innerComponent.getCapability(capability, facing)
    }

    override fun onRegister() {
        super.onRegister()
        innerComponent.wrapper = this.wrapper
    }
}