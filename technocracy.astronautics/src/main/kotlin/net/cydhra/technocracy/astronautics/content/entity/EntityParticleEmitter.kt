package net.cydhra.technocracy.astronautics.content.entity

import net.cydhra.technocracy.astronautics.content.fx.RefrectParticle
import net.cydhra.technocracy.foundation.api.fx.TCParticleManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.MoverType
import net.minecraft.util.DamageSource
import net.minecraft.world.World


class EntityParticleEmitter(world: World) : EntityLiving(world) {

    init {
        setSize(0.8f, 0.8f)
    }

    override fun onUpdate() {
        super.onUpdate()
        if (this.world.isRemote && this.ticksExisted % 10 == 0) {
            TCParticleManager.addParticle(RefrectParticle(world, posX  - 0.5, posY +1, posZ  - 0.5))
        }
    }

    override fun collideWithEntity(entityIn: Entity) {

    }

    override fun damageEntity(damageSrc: DamageSource, damageAmount: Float) {
        if (damageAmount > 0f) {
            this.setDead()
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
    }
}