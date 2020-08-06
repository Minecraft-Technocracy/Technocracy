package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.minecraft.block.material.Material
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.relauncher.Side


class WaterElectrolyzerLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumption = 5

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {

            val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage ?: return
            if (energy.currentEnergy < energyConsumption) return

            val player = logicParameters.player
            if (player.isInsideOfMaterial(Material.WATER)) {
                if (player.air <= 280) {
                    player.air = 300
                    if (logicParameters.side == Side.SERVER)
                        energy.consumeEnergy(energyConsumption)
                }

                if(logicParameters.side == Side.CLIENT && player.ticksExisted % 4 == 0) {
                    val f2: Float = player.rng.nextFloat() - player.rng.nextFloat()
                    val f1: Float = player.rng.nextFloat() - player.rng.nextFloat()
                    player.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, player.posX + f2.toDouble(), player.posY + player.getEyeHeight(), player.posZ + f1.toDouble(), player.motionX, player.motionY + 0.6, player.motionZ)
                }
            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}