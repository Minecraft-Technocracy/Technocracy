package net.cydhra.technocracy.foundation.integration.waila

import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.cydhra.technocracy.foundation.integration.waila.renderers.TCStringRenderer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
@WailaPlugin
class TCWailaClientPlugin : IWailaPlugin {

    override fun register(registrar: IWailaRegistrar?) {
        registrar!!.registerTooltipRenderer("technocracy.text", TCStringRenderer())
    }
}