package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.materials.*

open class CommonProxy : ISidedProxy {

    override fun preInit() {
        arrayOf(aluminiumSystem, copperSystem, leadSystem, nickelSystem, osmiumSystem, silverSystem, tinSystem)
                .forEach(MaterialSystem::register)
    }

    override fun init() {

    }

    override fun postInit() {

    }
}