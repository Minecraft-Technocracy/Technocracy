package net.cydhra.technocracy.astronautics.content.entity

import net.cydhra.technocracy.astronautics.client.render.entity.RenderEntityParticleEmitter
import net.cydhra.technocracy.astronautics.client.render.entity.RenderEntityRocket
import net.cydhra.technocracy.foundation.content.entities.util.EntityRegistryElement
import java.util.function.Function


val entityRocket = EntityRegistryElement("rocket", EntityRocket::class, 12, 224, Function { t -> RenderEntityRocket(t) })
val entityParticleEmitter = EntityRegistryElement("particle", EntityParticleEmitter::class, 12, 224, Function { t -> RenderEntityParticleEmitter(t) })