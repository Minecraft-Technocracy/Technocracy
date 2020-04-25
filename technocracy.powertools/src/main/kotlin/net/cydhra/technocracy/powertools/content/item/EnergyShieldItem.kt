package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.coremod.event.ItemCooldownEvent
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class EnergyShieldItem : BaseItem("energy_shield") {

    init {
        addPropertyOverride(ResourceLocation("blocking")) { stack, _, entityIn ->
            if (entityIn != null && entityIn.isHandActive && entityIn.activeItemStack == stack) {
                1.0f
            } else {
                0.0f
            }
        }
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onDamage(event: ItemCooldownEvent) {
        if(event.item == this) {
            event.delay = 50
        }
    }

    @SubscribeEvent
    fun onDamage(event: LivingAttackEvent) {
        val entity = event.entity
        //entity is using this shield
        val attacker = event.source.immediateSource
        if (entity is EntityLivingBase && attacker is EntityLivingBase &&
                entity.isHandActive && entity.activeItemStack.item == this &&
                !attacker.heldItemMainhand.item.canDisableShield(attacker.heldItemMainhand, entity.activeItemStack, attacker, entity)) {
            attacker.attackEntityFrom(DamageSource.causeThornsDamage(attacker), event.amount * 0.5f)
        }
    }

    override fun getMaxItemUseDuration(stack: ItemStack?): Int {
        return 72000
    }

    override fun isShield(stack: ItemStack, entity: EntityLivingBase?): Boolean {
        return true
    }

    override fun onItemRightClick(worldIn: World?, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        playerIn.activeHand = hand
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand))
    }

    override fun getItemUseAction(stack: ItemStack?): EnumAction? {
        return EnumAction.BLOCK
    }
}