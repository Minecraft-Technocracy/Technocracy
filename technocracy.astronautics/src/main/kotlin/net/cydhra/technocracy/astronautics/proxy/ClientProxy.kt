package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.foundation.tileentity.management.TileEntityManager

class ClientProxy : CommonProxy() {
    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
    }

}