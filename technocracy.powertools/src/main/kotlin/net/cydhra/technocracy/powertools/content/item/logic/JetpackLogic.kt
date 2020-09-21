package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.content.fx.ParticleSmoke
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager
import net.cydhra.technocracy.foundation.model.items.capability.getCapabilityWrapper
import net.cydhra.technocracy.foundation.util.getSide
import net.cydhra.technocracy.foundation.util.isBodyInsideOfMaterial
import net.cydhra.technocracy.powertools.content.item.upgrades.jetPackUpgrade
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import scala.xml.dtd.ContentModelParser.particle


class JetpackLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumption = 0

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        val player = logicParameters.player

        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {

            val prio = (logicParameters.data as EntityArmorTickData).priority
            if (prio != ItemStackTickPriority.LOW) return

            val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage
                    ?: run { player.disableFly(); return }

            if (energy.currentEnergy < energyConsumption) {
                player.disableFly(); return
            }
            if (player.isBodyInsideOfMaterial(Material.WATER)) {
                player.disableFly(); return
            }

            if (logicParameters.side == Side.SERVER) {
                if (player.capabilities.isFlying) {
                    energy.consumeEnergy(energyConsumption)
                    if (player.motionX != 0.0 || player.motionY != 0.0 || player.motionZ != 0.0) {
                        energy.consumeEnergy(energyConsumption)
                    }
                }
            } else {

                player.capabilities.flySpeed = 0.04f
                if (player.isSprinting)
                    player.capabilities.flySpeed /= 1.8f

                //going to async with server so we can fly and still take falldamage
                //this can cause users getting kicked if flying is not disabled but taking falldamage is important for balancing
                //simulating the falldamage is not possible as it could interfere with other mods
                player.capabilities.allowFlying = true

                if (player.capabilities.isFlying) {
                    spawnSmokeParticles(player)
                }
            }
        } else if (logicParameters.type == ItemStackTickType.EQUIP_STATE_CHANGE) {

            if (logicParameters.side == Side.SERVER) {
                val data = logicParameters.data as EquipmentData

                if (data.state == EquipmentData.EquipState.UNEQUIP) {
                    val wrapper = getCapabilityWrapper(data.to) ?: run {
                        player.disableFly(); return
                    }
                    val hasJetpack = wrapper.hasLogicStrategy(jetPackUpgrade.name)

                    if (!hasJetpack)
                        player.disableFly()
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private fun spawnSmokeParticles(player: EntityPlayer) {
        val userPos = player.getPositionEyes(0f).addVector(.0, -0.6, .0)

        var vLeft = Vec3d(-0.23, .0, -0.28)
        vLeft = vLeft.rotateYaw(Math.toRadians(((-player.renderYawOffset).toDouble())).toFloat())

        var vRight = Vec3d(0.23, .0, -0.28)
        vRight = vRight.rotateYaw(Math.toRadians(((-player.renderYawOffset).toDouble())).toFloat())

        vLeft = vLeft.addVector(-player.motionX * 0.2, -player.motionY * 0.2, -player.motionZ * 0.2)
        vRight = vRight.addVector(-player.motionX * 0.2, -player.motionY * 0.2, -player.motionZ * 0.2)


        val v = userPos.add(vLeft)
        val v2 = userPos.add(vRight)

        for (i in 0..3) {
            val ps1 = ParticleSmoke(player.world, v.x, v.y, v.z)
            ps1.size = 0.2f + (player.rng.nextFloat() - player.rng.nextFloat()) / 5f
            ps1.setAge(12)
            ps1.setMaxAge(5 * 20)
            ps1.setMotionX(ps1.getMotionX() / 5.0)
            ps1.setMotionZ(ps1.getMotionZ() / 5.0)
            ps1.setMotionY(player.motionY - 0.2)
            TCParticleManager.addParticle(ps1)


            val ps2 = ParticleSmoke(player.world, v2.x, v2.y, v2.z)
            ps2.size = 0.2f + (player.rng.nextFloat() - player.rng.nextFloat()) / 5f
            ps2.setMaxAge(5 * 20)
            ps2.setAge(12)
            ps2.setMotionX(ps1.getMotionX() / 5.0)
            ps2.setMotionZ(ps1.getMotionZ() / 5.0)
            ps2.setMotionY(player.motionY - 0.2)
            TCParticleManager.addParticle(ps2)
        }
    }

    private fun EntityPlayer.disableFly() {
        if (!isCreative) {
            capabilities.allowFlying = false
            capabilities.isFlying = false
            if (getSide() == Side.CLIENT)
                capabilities.flySpeed = 0.05f
            sendPlayerAbilities()
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}