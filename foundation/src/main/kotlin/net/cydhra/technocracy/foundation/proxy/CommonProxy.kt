package net.cydhra.technocracy.foundation.proxy

import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.items.general.*
import net.cydhra.technocracy.foundation.materials.*

open class CommonProxy : ISidedProxy {

    private val materialSystems = arrayOf(aluminiumSystem, copperSystem, leadSystem,
            lithiumSystem, nickelSystem, osmiumSystem, silverSystem, tinSystem)

    override fun preInit() {
        materialSystems.forEach(MaterialSystem::preInit)

        BlockManager.prepareBlocksForRegistration(pulverizerBlock)
        BlockManager.prepareBlocksForRegistration(electricFurnaceBlock)
        BlockManager.prepareBlocksForRegistration(centrifugeBlock)
        BlockManager.prepareBlocksForRegistration(oilSandBlock)
        BlockManager.prepareBlocksForRegistration(ironBeamBlock)

        ItemManager.prepareItemForRegistration(machineFrameItem)
        ItemManager.prepareItemForRegistration(coalDustItem)
        ItemManager.prepareItemForRegistration(ironDustItem)
        ItemManager.prepareItemForRegistration(ironSheetItem)

        ItemManager.prepareItemForRegistration(batteryItem)
        ItemManager.prepareItemForRegistration(akkumulatorItem)
    }

    override fun init() {
        materialSystems.forEach(MaterialSystem::init)
    }

    override fun postInit() {

    }
}