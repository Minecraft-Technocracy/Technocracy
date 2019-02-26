package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.blocks.BlockManager
import net.cydhra.technocracy.foundation.items.ItemManager

class ClientProxy : CommonProxy() {

    override fun preInit() {
        super.preInit()
    }

    override fun init() {
        super.init()
        ItemManager.registerItemColors()
        BlockManager.registerBlockColors()
    }

    override fun postInit() {
        super.postInit()
    }
}