package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.api.ecs.logic.UnequipData
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect


class NightVisionLogic : ILogic<ItemStackLogicParameters> {
    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            val player = logicParameters.player
            val currentEffect = player.getActivePotionEffect(MobEffects.NIGHT_VISION)

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

                player.addPotionEffect(PotionEffect(MobEffects.NIGHT_VISION, 420, -10, true, false))
            }

            //todo not enought energy remove nv effect
            //todo energy cost?
        } else if (logicParameters.type == ItemStackTickType.UNEQUIP) {
            val data = logicParameters.data as UnequipData
            //only remove effect if it was on armor element
            if (!data.armor) return

            val player = logicParameters.player
            val currentEffect = player.getActivePotionEffect(MobEffects.NIGHT_VISION) ?: return

            //remove only if it is our effect
            if (currentEffect.amplifier == -10) {
                player.removePotionEffect(MobEffects.NIGHT_VISION)
                player.removeActivePotionEffect(MobEffects.NIGHT_VISION)
            }
        }

    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}