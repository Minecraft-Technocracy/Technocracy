package net.cydhra.technocracy.foundation.potions

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.potion.Potion
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


/**
 * This Manager object is responsible for collecting potion effects and registering them in registering phase.
 *
 * @see ItemManager.prepareItemForRegistration
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object PotionManager {

    /**
     * Potions scheduled for registering
     */
    private val potionsToRegister = mutableListOf<BasePotion>()

    /**
     * Schedule an potion for registration. Registration will be done, as soon as the registration event marks
     * registration phase.
     */
    fun preparePotionForRegistration(potion: BasePotion) {
        potionsToRegister += potion
    }

    @Suppress("unused")
    @SubscribeEvent
    @JvmStatic
    fun registerPotions(event: RegistryEvent.Register<Potion>) {
        event.registry.registerAll(*potionsToRegister.toTypedArray())
    }
}