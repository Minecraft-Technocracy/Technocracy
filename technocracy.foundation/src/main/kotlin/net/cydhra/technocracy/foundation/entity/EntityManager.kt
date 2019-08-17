package net.cydhra.technocracy.foundation.entity

import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.EntityEntryBuilder
import kotlin.reflect.KClass


class EntityManager(val modName: String) {
    /**
     * A list of all prepared entities that are registered during entity registration phase
     */
    private val preparedEntities = mutableMapOf<IBaseEntity, KClass<out Entity>>()

    private val associatedRenderers = mutableMapOf<KClass<out Entity>, Render<*>>()

    fun <T : Entity> prepareTileEntityForRegistration(entityClass: IBaseEntity, baseEntity: KClass<out T>,
                                                      entityRenderer: Render<*>? = null) {
        this.preparedEntities += entityClass to baseEntity

        if (entityRenderer != null)
            this.associatedRenderers[baseEntity] = entityRenderer
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(@Suppress("UNUSED_PARAMETER") event: RegistryEvent.Register<EntityEntry>) {

        var index = 0
        for (pair in preparedEntities) {
            val entry = EntityEntryBuilder.create<Entity>().
                    entity(pair.value.java).
                    name(pair.key.getName()).
                    id(ResourceLocation(modName, pair.key.getName()), index++).
                    egg(pair.key.primaryColor, pair.key.secondaryColor).
                    tracker(64 ,20, true).build()
            event.registry.register(entry)
        }
    }
}