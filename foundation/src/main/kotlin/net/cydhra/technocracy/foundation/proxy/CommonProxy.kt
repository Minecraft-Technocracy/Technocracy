package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.blocks.BlockManager
import net.cydhra.technocracy.foundation.blocks.Pulverizer
import net.cydhra.technocracy.foundation.materials.*

open class CommonProxy : ISidedProxy {

    private val materialSystems = arrayOf(aluminiumSystem, copperSystem, leadSystem,
            lithiumSystem, nickelSystem, osmiumSystem, silverSystem, tinSystem)

    override fun preInit() {
        materialSystems.forEach(MaterialSystem::preInit)

        BlockManager.prepareBlocksForRegistration(pulverizerBlock)
        BlockManager.prepareBlocksForRegistration(oilSandBlock)
    }

    override fun init() {
        materialSystems.forEach(MaterialSystem::init)
    }

    override fun postInit() {

    }
}