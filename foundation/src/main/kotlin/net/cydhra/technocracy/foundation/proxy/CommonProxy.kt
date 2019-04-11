package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.blocks.general.oilSandBlock
import net.cydhra.technocracy.foundation.blocks.general.pulverizerBlock
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.items.general.coalDustItem
import net.cydhra.technocracy.foundation.materials.*

open class CommonProxy : ISidedProxy {

    private val materialSystems = arrayOf(aluminiumSystem, copperSystem, leadSystem,
            lithiumSystem, nickelSystem, osmiumSystem, silverSystem, tinSystem)

    override fun preInit() {
        materialSystems.forEach(MaterialSystem::preInit)

        BlockManager.prepareBlocksForRegistration(pulverizerBlock)
        BlockManager.prepareBlocksForRegistration(oilSandBlock)

        ItemManager.prepareItemForRegistration(coalDustItem)
    }

    override fun init() {
        materialSystems.forEach(MaterialSystem::init)
    }

    override fun postInit() {

    }
}