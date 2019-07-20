package net.cydhra.technocracy.foundation.integration.waila

import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.cydhra.technocracy.foundation.blocks.MachineBlock
import net.cydhra.technocracy.foundation.integration.waila.providers.MachineProvider

@WailaPlugin
class TCWailaPlugin : IWailaPlugin {

    val machineProvider  = MachineProvider()

    override fun register(registrar: IWailaRegistrar?) {
        registrar!!
        registrar.registerBodyProvider(machineProvider, MachineBlock::class.java)
        registrar.registerNBTProvider(machineProvider, MachineBlock::class.java)
    }
}