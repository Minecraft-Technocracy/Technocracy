package net.cydhra.technocracy.foundation.potions

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.ThreadLocalRandom

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
class OilyEffect : BasePotion("oily", true, 0x1A1A1A) {

    override fun hasStatusIcon(): Boolean {
        return false
    }

    @Suppress("unused")
    @SubscribeEvent
    fun equipmentChange(event: LivingEquipmentChangeEvent) {
        if (event.entityLiving is EntityPlayer) {
            if (event.entityLiving.isPotionActive(this)) {
                if (ThreadLocalRandom.current().nextInt(100) < 25) {
                    val player = event.entityLiving as EntityPlayer
                    val stack = event.to

                    if (event.slot.slotType == EntityEquipmentSlot.Type.ARMOR) {
                        if (stack.item.onDroppedByPlayer(stack, player)) {
                            net.minecraftforge.common.ForgeHooks.onPlayerTossEvent(player, stack, true)
                            player.inventory.armorInventory[event.slot.index] = ItemStack.EMPTY
                        }
                    } else {
                        player.dropItem(true)
                    }
                }
            }
        }
    }

    @Suppress("unused")
    @SubscribeEvent
    fun livingUpdate(event: LivingEvent.LivingUpdateEvent) {
        if (event.entityLiving is EntityPlayer) {
            if (event.entityLiving.isPotionActive(this)) {
                val player = event.entityLiving as EntityPlayer

                if(player.onGround) {
                    player.motionX *= 0.91F * 0.91
                    player.motionZ *= 0.91F * 0.91
                }
            }
        }
    }
}