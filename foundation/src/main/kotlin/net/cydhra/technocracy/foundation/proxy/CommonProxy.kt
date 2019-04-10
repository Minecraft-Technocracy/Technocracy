package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.blocks.BlockManager
import net.cydhra.technocracy.foundation.blocks.Pulveriser
import net.cydhra.technocracy.foundation.materials.*

open class CommonProxy : ISidedProxy {

    private val materialSystems = arrayOf(aluminiumSystem, copperSystem, leadSystem,
            lithiumSystem, nickelSystem, osmiumSystem, silverSystem, tinSystem)

    override fun preInit() {
        materialSystems.forEach(MaterialSystem::preInit)

        BlockManager.prepareBlocksForRegistration(Pulverizer())
    }

    override fun init() {
        materialSystems.forEach(MaterialSystem::init)
    }

    override fun postInit() {

    }
}