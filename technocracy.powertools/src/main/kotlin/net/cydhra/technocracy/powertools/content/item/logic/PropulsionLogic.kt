package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemMultiplierComponent
import net.cydhra.technocracy.foundation.util.isBodyInsideOfMaterial
import net.minecraft.block.material.Material
import net.minecraftforge.fml.relauncher.Side
import kotlin.math.max


class PropulsionLogic(private val multiplier: ItemMultiplierComponent) : ILogic<ItemStackLogicParameters> {

    val energyConsumption = 0

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        val player = logicParameters.player

        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            val prio = (logicParameters.data as EntityArmorTickData).priority
            if (prio != ItemStackTickPriority.MEDIUM) return

            val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage ?: return

            if (energy.currentEnergy < energyConsumption) {
                return
            }
            if (player.isBodyInsideOfMaterial(Material.WATER)) {
                return
            }

            val multiplier = max(multiplier.multiplier, 4.0).toFloat()

            if (logicParameters.side == Side.SERVER) {
                if (player.capabilities.isFlying) {
                    if (player.motionX != 0.0 || player.motionY != 0.0 || player.motionZ != 0.0) {
                        energy.consumeEnergy(energyConsumption * multiplier.toInt())
                    }
                }
            } else {
                player.capabilities.flySpeed = 0.04f + 0.01f * multiplier
            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}