package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.minecraft.block.material.Material
import net.minecraft.potion.Potion


class WaterElectrolyzerLogic : ILogic<ItemStackLogicParameters> {
    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            val player = logicParameters.player
            if (player.isInsideOfMaterial(Material.WATER)) {
                if (player.air <= 280) {
                    player.air = 300
                }
            }
            //todo energy cost?
        }

        Potion.REGISTRY
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}