package net.cydhra.technocracy.foundation.model.entities.util

import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.Entity
import java.util.function.Function
import kotlin.reflect.KClass


class EntityRegistryElement<T : Entity>(val name: String, val entity: KClass<T>, val primaryColor: Int = -1, val secondaryColor: Int = -1, val entityRender: Function<RenderManager, Render<T>>? = null)