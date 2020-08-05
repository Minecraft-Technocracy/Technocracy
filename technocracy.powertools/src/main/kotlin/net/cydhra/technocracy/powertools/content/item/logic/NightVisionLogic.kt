package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.api.ecs.logic.EquipmentData
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.model.items.capability.getCapabilityWrapper
import net.cydhra.technocracy.powertools.content.item.upgrades.jetPackUpgrade
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraftforge.fml.relauncher.Side


class NightVisionLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumtion = 5

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {

        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            val serverside = logicParameters.side == Side.SERVER

            val e = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>() ?: return

            val energy = e.energyStorage

            val player = logicParameters.player
            val currentEffect = player.getActivePotionEffect(MobEffects.NIGHT_VISION)

            if (energy.currentEnergy < energyConsumtion) {
                //not enought energy remove effect
                //remove only if it is our effect
                if (currentEffect != null && currentEffect.amplifier == -10) {
                    player.removePotionEffect(MobEffects.NIGHT_VISION)
                    player.removeActivePotionEffect(MobEffects.NIGHT_VISION)
                }
                return
            }


            //give -10 so it can be overridden by any normal effect
            //we also can identify the effect by this magic value so we can remove it later on
            if (currentEffect == null || currentEffect.duration <= 20 * 15) {
                //if potion effect is present and is running out, but its not our one
                //just remove it to prevent flickering
                //can happen if you rejoin a world and the server sends the wrong amplifier
                if (currentEffect != null && currentEffect.amplifier != -10) {
                    player.removePotionEffect(MobEffects.NIGHT_VISION)
                    player.removeActivePotionEffect(MobEffects.NIGHT_VISION)
                }
                if (serverside)
                    energy.consumeEnergy(energyConsumtion)
                player.addPotionEffect(PotionEffect(MobEffects.NIGHT_VISION, 420, -10, true, false))
            }

        } else if (logicParameters.type == ItemStackTickType.EQUIP_STATE_CHANGE) {
            val data = logicParameters.data as EquipmentData
            //only remove effect if it was on armor element
            if (!data.armor) return

            val player = logicParameters.player

            //remove if unequip
            if (data.state == EquipmentData.EquipState.UNEQUIP) {
                val wrapper = getCapabilityWrapper(data.to) ?: return
                val hasNightVision = wrapper.hasLogicStrategy(jetPackUpgrade.name)

                if (!hasNightVision) {
                    val currentEffect = player.getActivePotionEffect(MobEffects.NIGHT_VISION) ?: return
                    //remove only if it is our effect
                    if (currentEffect.amplifier == -10) {
                        player.removePotionEffect(MobEffects.NIGHT_VISION)
                        player.removeActivePotionEffect(MobEffects.NIGHT_VISION)
                    }
                }
            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}