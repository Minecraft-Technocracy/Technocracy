package net.cydhra.technocracy.foundation.model.entities.manager

import net.cydhra.technocracy.foundation.model.entities.util.EntityRegistryElement
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.EntityEntryBuilder


class EntityManager(val modName: String) {
    /**
     * A list of all prepared entities that are registered during entity registration phase
     */
    private val preparedEntities = mutableListOf<EntityRegistryElement<out Entity>>()

    fun prepareEntityForRegistration(element: EntityRegistryElement<out Entity>) {
        this.preparedEntities += element
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(@Suppress("UNUSED_PARAMETER") event: RegistryEvent.Register<EntityEntry>) {

        preparedEntities.forEachIndexed { index, element ->
            val entry = EntityEntryBuilder.create<Entity>().entity(element.entity.java).name(element.name).id(ResourceLocation(modName, element.name), index).tracker(64, 20, true)

            if (element.primaryColor != -1) {
                entry.egg(element.primaryColor, element.secondaryColor)
            }

            event.registry.register(entry.build())
        }
    }

    fun registerRenderer() {
        preparedEntities.filter { it.entityRender != null }.forEach {
            RenderingRegistry.registerEntityRenderingHandler(it.entity.java) { manager ->
                @Suppress("UNCHECKED_CAST") // circumvent type erasure
                it.entityRender!!.apply(manager) as Render<in Entity>?
            }
        }
    }
}