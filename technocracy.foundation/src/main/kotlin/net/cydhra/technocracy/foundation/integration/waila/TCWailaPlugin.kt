package net.cydhra.technocracy.foundation.integration.waila

import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractBaseBlock
import net.cydhra.technocracy.foundation.integration.waila.providers.MachineWailaProvider

@WailaPlugin
class TCWailaPlugin : IWailaPlugin {

    val machineProvider = MachineWailaProvider()

    override fun register(registrar: IWailaRegistrar) {
        registrar.registerBodyProvider(machineProvider, AbstractBaseBlock::class.java)
        registrar.registerNBTProvider(machineProvider, AbstractBaseBlock::class.java)
    }
}