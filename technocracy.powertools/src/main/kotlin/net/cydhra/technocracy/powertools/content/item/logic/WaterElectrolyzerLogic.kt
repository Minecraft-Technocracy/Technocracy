package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.minecraft.block.material.Material
import net.minecraft.potion.Potion
import net.minecraftforge.fml.relauncher.Side


class WaterElectrolyzerLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumtion = 5

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {

            val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage ?: return
            if (energy.currentEnergy < energyConsumtion) return

            val player = logicParameters.player
            if (player.isInsideOfMaterial(Material.WATER)) {
                if (player.air <= 280) {
                    player.air = 300
                    if (logicParameters.side == Side.SERVER)
                        energy.consumeEnergy(energyConsumtion)
                }
            }
            //todo energy cost?
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}