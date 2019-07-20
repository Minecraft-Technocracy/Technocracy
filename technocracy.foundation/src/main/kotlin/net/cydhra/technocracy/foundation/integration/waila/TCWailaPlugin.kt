package net.cydhra.technocracy.foundation.integration.waila

import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.cydhra.technocracy.foundation.blocks.MachineBlock
import net.cydhra.technocracy.foundation.blocks.MultiBlockRotatableActiveBlock
import net.cydhra.technocracy.foundation.integration.waila.providers.MachineWailaProvider
import net.cydhra.technocracy.foundation.integration.waila.providers.MultiBlockWailaProvider

@WailaPlugin
class TCWailaPlugin : IWailaPlugin {

    val machineProvider = MachineWailaProvider()
    val multiBlockProvider = MultiBlockWailaProvider()

    override fun register(registrar: IWailaRegistrar) {
        registrar.registerBodyProvider(machineProvider, MachineBlock::class.java)
        registrar.registerNBTProvider(machineProvider, MachineBlock::class.java)

        registrar.registerBodyProvider(multiBlockProvider, MultiBlockRotatableActiveBlock::class.java)
        registrar.registerNBTProvider(multiBlockProvider, MultiBlockRotatableActiveBlock::class.java)
    }
}