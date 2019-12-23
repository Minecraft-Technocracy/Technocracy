package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.cydhra.technocracy.foundation.util.compound
import net.minecraft.nbt.NBTTagCompound


class OwnerShipTileEntityComponent : AbstractTileEntityComponent() {

    var currentOwner: GroupManager.PlayerGroup? = null
        private set

    fun setOwnerShip(ownerShip: GroupManager.PlayerGroup) {
        currentOwner = ownerShip
        markDirty(false)
    }

    override fun serializeNBT(): NBTTagCompound {
        if (currentOwner != null) {
            return compound {
                "ownerGroup" to currentOwner!!.groupId
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