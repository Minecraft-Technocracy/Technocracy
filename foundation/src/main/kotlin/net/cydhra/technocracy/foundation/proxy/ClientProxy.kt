package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.items.general.ItemManager

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