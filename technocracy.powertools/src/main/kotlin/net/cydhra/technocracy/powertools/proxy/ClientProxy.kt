package net.cydhra.technocracy.powertools.proxy


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
    }
}