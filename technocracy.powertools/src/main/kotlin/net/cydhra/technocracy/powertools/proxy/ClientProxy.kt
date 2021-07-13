package net.cydhra.technocracy.powertools.proxy

import net.cydhra.technocracy.powertools.client.FlashLightRenderer
import net.cydhra.technocracy.powertools.client.ShieldRenderer
import net.cydhra.technocracy.powertools.content.item.energyShield


class ClientProxy : CommonProxy() {
    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
        tileEntityManager.onClientInitialize()

    }

    override fun preInit() {
        super.preInit()
        blockManager.initClient()
        itemManager.initClient()
        tileEntityManager.initClient()
        entityManager.registerRenderer()
        energyShield.tileEntityItemStackRenderer = ShieldRenderer
    }
}