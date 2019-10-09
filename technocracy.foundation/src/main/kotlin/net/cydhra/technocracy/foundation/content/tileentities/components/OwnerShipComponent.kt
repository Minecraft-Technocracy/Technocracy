package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.cydhra.technocracy.foundation.util.compound
import net.minecraft.nbt.NBTTagCompound


class OwnerShipComponent : AbstractComponent() {

    var currentOwner: GroupManager.PlayerGroup? = null
        private set

    fun setOwnerShip(ownerShip: GroupManager.PlayerGroup) {
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
            currentOwner = GroupManager.getGroup(nbt.getUniqueId("ownerGroup")!!)
    }

    override val type: ComponentType = ComponentType.OTHER
}