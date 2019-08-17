package net.cydhra.technocracy.foundation.entity

import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.EntityEntryBuilder


class EntityManager(val modName: String) {
    /**
     * A list of all prepared entities that are registered during entity registration phase
     */
    private val preparedEntities = mutableListOf<EntityRegistryElement>()

    private val associatedRenderers = mutableMapOf<EntityRegistryElement, Render<*>>()

    fun prepareEntityForRegistration(element: EntityRegistryElement, entityRenderer: Render<Entity>? = null) {
        this.preparedEntities += element

        if (entityRenderer != null)
            this.associatedRenderers[element] = entityRenderer
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
}