package net.cydhra.technocracy.foundation.entity

import net.minecraft.entity.Entity
import kotlin.reflect.KClass


class EntityRegistryElement(val name: String, val entity: KClass<out Entity>, val primaryColor: Int = -1, val secondaryColor: Int = -1)