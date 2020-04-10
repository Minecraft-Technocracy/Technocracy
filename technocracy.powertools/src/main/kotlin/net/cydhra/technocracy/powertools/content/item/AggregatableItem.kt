package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.ecs.item.TCAggregatableItem
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.minecraft.nbt.NBTTagCompound

/**
 * An item that is an aggregation of multiple [IComponent] instances that define state associated with stacks of this
 * item. State is generally used by different [net.cydhra.technocracy.foundation.api.ecs.logic.ILogic] for stateful
 * behavior of that specific item stack.
 *
 * @param unlocalizedName the unlocalized name of the item
 */
class AggregatableItem(registryName: String, unlocalizedName: String = registryName)
    : BaseItem(registryName, unlocalizedName), TCAggregatableItem {

    private val registeredComponents = mutableMapOf<String, IComponent>()

    override fun getComponents(): List<Pair<String, IComponent>> {
        return registeredComponents.toList()
    }

    override fun registerComponent(component: IComponent, name: String) {
        this.registeredComponents.putIfAbsent(name, component)
                ?: throw IllegalArgumentException("a component with this name has already been registered")

        component.onRegister()
    }

    override fun removeComponent(name: String) {
        this.registeredComponents.remove(name)
    }

    override fun serializeNBT(compound: NBTTagCompound): NBTTagCompound {
        for ((name, component) in registeredComponents) {
            compound.setTag(name, component.serializeNBT())
        }
        return compound
    }

    override fun deserializeNBT(compound: NBTTagCompound) {
        for (key in registeredComponents.keys) {
            val component = registeredComponents[key]!!
            if (compound.hasKey(key))
                component.deserializeNBT(compound.getCompoundTag(key))
            component.onLoadAggregate()
        }
    }

}