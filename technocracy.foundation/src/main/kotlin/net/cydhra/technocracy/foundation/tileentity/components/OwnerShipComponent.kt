package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.data.OwnershipManager
import net.cydhra.technocracy.foundation.util.compound
import net.minecraft.nbt.NBTTagCompound


class OwnerShipComponent : AbstractComponent() {

    var currentOwner: OwnershipManager.Ownership? = null
        private set

    fun setOwnerShip(ownerShip: OwnershipManager.Ownership) {
        currentOwner = ownerShip
        markDirty(false)
    }

    override fun serializeNBT(): NBTTagCompound {
        if (currentOwner != null) {
            return compound {
                "ownerGroup" to currentOwner!!.ownerShipUUID
            }
        }
        return NBTTagCompound()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasUniqueId("ownerGroup"))
            currentOwner = OwnershipManager.getGroup(nbt.getUniqueId("ownerGroup")!!)
    }

    override val type: ComponentType = ComponentType.OTHER
}