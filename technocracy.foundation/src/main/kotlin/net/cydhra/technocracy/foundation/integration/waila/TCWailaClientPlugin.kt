package net.cydhra.technocracy.foundation.integration.waila

import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.IWailaRegistrar
import mcp.mobius.waila.api.WailaPlugin
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.integration.waila.renderers.TCEnergyRenderer
import net.cydhra.technocracy.foundation.integration.waila.renderers.TCFluidRenderer
import net.cydhra.technocracy.foundation.integration.waila.renderers.TCStringRenderer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
@WailaPlugin
class TCWailaClientPlugin : IWailaPlugin {

    override fun register(registrar: IWailaRegistrar) {
        registrar.registerTooltipRenderer("${TCFoundation.MODID}.text", TCStringRenderer())
        registrar.registerTooltipRenderer("${TCFoundation.MODID}.energy", TCEnergyRenderer())
        registrar.registerTooltipRenderer("${TCFoundation.MODID}.fluid", TCFluidRenderer())
    }
}