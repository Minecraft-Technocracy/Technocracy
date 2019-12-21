package net.cydhra.technocracy.astronautics.content.entity

import net.cydhra.technocracy.astronautics.content.fx.ParticleSmoke
import net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.MoverType
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumHandSide
import net.minecraft.world.World


class EntityParticleEmitter(world: World) : EntityLiving(world) {

    init {
        setSize(0.8f,0.8f)
    }

    override fun onUpdate() {
        super.onUpdate()
        if (this.world.isRemote) {
            TCParticleManager.addParticle(ParticleSmoke(world, posX + rand.nextFloat() - 0.5, posY + rand.nextDouble(), posZ + rand.nextFloat() - 0.5))
        }
    }

    override fun collideWithEntity(entityIn: Entity) {

    }

    override fun damageEntity(damageSrc: DamageSource, damageAmount: Float) {
        if(damageAmount > 0f) {
            this.setDead()
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
    }
}