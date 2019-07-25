package net.cydhra.technocracy.foundation.integration.waila

import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.cydhra.technocracy.foundation.blocks.MachineBlock
import net.cydhra.technocracy.foundation.blocks.MultiBlockRotatableActiveBlock
import net.cydhra.technocracy.foundation.blocks.PlainMultiBlockPartBlock
import net.cydhra.technocracy.foundation.integration.waila.providers.MachineWailaProvider

@WailaPlugin
class TCWailaPlugin : IWailaPlugin {

    val machineProvider = MachineWailaProvider()

    override fun register(registrar: IWailaRegistrar) {
        // machines
        registrar.registerBodyProvider(machineProvider, MachineBlock::class.java)
        registrar.registerNBTProvider(machineProvider, MachineBlock::class.java)

        // multiblocks
        registrar.registerBodyProvider(machineProvider, MultiBlockRotatableActiveBlock::class.java)
        registrar.registerNBTProvider(machineProvider, MultiBlockRotatableActiveBlock::class.java)
        registrar.registerBodyProvider(machineProvider, PlainMultiBlockPartBlock::class.java)
        registrar.registerNBTProvider(machineProvider, PlainMultiBlockPartBlock::class.java)
    }
}