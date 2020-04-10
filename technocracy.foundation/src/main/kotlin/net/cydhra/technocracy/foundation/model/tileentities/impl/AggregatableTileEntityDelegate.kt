package net.cydhra.technocracy.foundation.model.tileentities.impl

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractCapabilityTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Delegate implementation of [TCAggregatableTileEntity] that can be used by tile entities to reduce code duplication
 */
class AggregatableTileEntityDelegate : TCAggregatableTileEntity {

    override lateinit var tile: TileEntity

    /**
     * All machine components that are saved to NBT and possibly accessible from GUI
     */
    private val components: MutableList<Pair<String, AbstractTileEntityComponent>> = mutableListOf()

    /**
     * All components that also offer a capability. They must also be added to [components] but for speed they are
     * also collected in this list for quick query times in [supportsCapability]
     */
    private val capabilityComponents: MutableMap<String, AbstractCapabilityTileEntityComponent> = mutableMapOf()

    /**
     * @return all registered components
     */
    override fun getComponents(): List<Pair<String, IComponent>> {
        return this.components
    }

    /**
     * Register a machine component. Should happen during construction of the tile entity instance.
     *
     * @param component [AbstractTileEntityComponent] implementation
     * @param name machine-unique name for the component. Used in NBT serialization
     */
    override fun registerComponent(component: IComponent, name: String) {
        if (component !is AbstractTileEntityComponent) {
            throw IllegalArgumentException("only AbstractComponents of tile entities are allowed")
        }

        component.tile = tile

        if (this.components.any { (componentName, _) -> componentName == name }) {
            throw IllegalArgumentException("cannot register the same component twice")
        }

        this.components += name to component

        component.onRegister()

        if (component is AbstractCapabilityTileEntityComponent) {
            capabilityComponents[name] = component
        }
    }

    override fun removeComponent(name: String) {
        this.components.removeIf { (componentName, _) -> componentName == name }
        this.capabilityComponents.remove(name)
    }

    override fun serializeNBT(compound: NBTTagCompound): NBTTagCompound {
        for ((name, component) in components) {
            compound.setTag(name, component.serializeNBT())
        }
        return compound
    }

    override fun deserializeNBT(compound: NBTTagCompound) {
        var i = 0
        while (i < components.size) {
            val (name, component) = components[i++]
            if (compound.hasKey(name))
                component.deserializeNBT(compound.getCompoundTag(name))
            component.onLoadAggregate()
        }
    }

    override fun supportsCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capabilityComponents.values.any { it.hasCapability(capability, facing) }
    }

    override fun <T : Any?> castCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return capabilityComponents.values
                .firstOrNull { it.hasCapability(capability, facing) }
                ?.getCapability(capability, facing)
    }

    override fun generateNbtUpdateCompound(player: EntityPlayerMP, tag: NBTTagCompound): NBTTagCompound {
        val compound = NBTTagCompound()
        val components = NBTTagList()
        this.getComponents()
                .filter { it.second.type.supportsWaila }
                .forEach { (name, component) ->
                    val index = NBTTagCompound()
                    index.setInteger("index", components.tagCount())
                    index.setInteger("type", component.type.ordinal)

                    compound.setTag(name, index)
                    components.appendTag(component.serializeNBT())
                }

        compound.setTag("list", components)
        tag.setTag(TCFoundation.MODID, compound)
        return tag
    }
}