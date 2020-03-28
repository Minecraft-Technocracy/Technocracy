package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.client.model.concreteSprayer.ConcreteSprayerItemModel
import net.cydhra.technocracy.astronautics.content.items.concreteSprayerItem


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

        itemManager.linkItemToModel(concreteSprayerItem, ConcreteSprayerItemModel())

        entityManager.registerRenderer()
    }
}