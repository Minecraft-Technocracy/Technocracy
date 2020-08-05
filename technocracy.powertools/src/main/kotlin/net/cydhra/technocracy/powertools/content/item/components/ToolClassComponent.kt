package net.cydhra.technocracy.powertools.content.item.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.cydhra.technocracy.foundation.util.compound
import net.cydhra.technocracy.foundation.util.get
import net.minecraft.nbt.NBTTagCompound

/**
 * A component that stores levels for given tool classes. This way an item can be assigned different tool classes.
 */
class ToolClassComponent : AbstractItemComponent() {
    override val type: ComponentType = ComponentType.OTHER

    val toolClasses = mutableMapOf<String, Int>()

    override fun serializeNBT(): NBTTagCompound {
        return compound {
            toolClasses.keys.forEach { cls ->
                cls to toolClasses[cls]!!
            }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        nbt.keySet.forEach { cls ->
            toolClasses[cls] = nbt[cls]
        }
    }
}