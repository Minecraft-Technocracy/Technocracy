package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.EquipmentData
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.model.items.capability.getCapabilityWrapper
import net.cydhra.technocracy.powertools.content.item.upgrades.jetPackUpgrade
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side


class JetpackLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumtion = 0

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {

        val player = logicParameters.player

        if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage
                    ?: run { player.disableFly(); return }
            if (energy.currentEnergy < energyConsumtion) run { player.disableFly(); return }
            player.capabilities.allowFlying = true
            player.capabilities.flySpeed = 0.04f

            if (player.isSprinting)
                player.capabilities.flySpeed /= 1.8f

            if (player.capabilities.isFlying) {
                if (logicParameters.side == Side.SERVER) {
                    energy.consumeEnergy(energyConsumtion)
                    if (player.motionX != 0.0 || player.motionY != 0.0 || player.motionZ != 0.0) {
                        energy.consumeEnergy(energyConsumtion)
                    }
                }
            }
        } else if (logicParameters.type == ItemStackTickType.EQUIP_STATE_CHANGE) {
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

    private fun EntityPlayer.disableFly() {
        if (!isCreative) {
            capabilities.allowFlying = false
            capabilities.isFlying = false
            capabilities.flySpeed = 0.05f
            sendPlayerAbilities()
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}