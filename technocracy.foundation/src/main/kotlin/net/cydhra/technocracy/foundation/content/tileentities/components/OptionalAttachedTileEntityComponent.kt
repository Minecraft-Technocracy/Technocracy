package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * A wrapping component that wraps another component that can be attached optionally. If [isAttached] is set to
 * false, the wrapped component will be ignored, otherwise the wrapped component is added in a transparent fashion.
 * This means that capabilities are exposed if present. This does however not mean, that logic can handle
 * this component instead of the wrapped one: The type is still opaque.
 */
class OptionalAttachedTileEntityComponent<T : AbstractTileEntityComponent>(val innerComponent: T) : AbstractCapabilityTileEntityComponent() {

    var isAttached = false
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
        return isAttached && innerComponent is AbstractCapabilityTileEntityComponent &&
                innerComponent.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (!isAttached || innerComponent !is AbstractCapabilityTileEntityComponent) {
            return null
        }

        return innerComponent.getCapability(capability, facing)
    }

    override fun onRegister() {
        super.onRegister()
        innerComponent.tile = this.tile
    }
}