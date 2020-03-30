package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.client.model.concreteSprayer.ConcreteSprayerItemModel
import net.cydhra.technocracy.astronautics.client.renderer.CustomSkyRenderer
import net.cydhra.technocracy.astronautics.content.items.concreteSprayerItem
import net.minecraftforge.common.MinecraftForge


class ClientProxy : CommonProxy() {
    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
        tileEntityManager.onClientInitialize()
    }

    override fun preInit() {
        super.preInit()

        MinecraftForge.EVENT_BUS.register(CustomSkyRenderer)

        blockManager.initClient()
        itemManager.initClient()
        tileEntityManager.initClient()

        itemManager.linkItemToModel(concreteSprayerItem, ConcreteSprayerItemModel())

        entityManager.registerRenderer()
    }
}