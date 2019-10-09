package net.cydhra.technocracy.astronautics.entity

import net.cydhra.technocracy.astronautics.client.render.entity.RenderEntityRocket
import net.cydhra.technocracy.foundation.model.entities.util.EntityRegistryElement
import java.util.function.Function


val entityRocket = EntityRegistryElement("rocket", EntityRocket::class, 12, 224, Function { t -> RenderEntityRocket(t) })