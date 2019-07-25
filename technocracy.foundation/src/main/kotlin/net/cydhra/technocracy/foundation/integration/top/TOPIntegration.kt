package net.cydhra.technocracy.foundation.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.cydhra.technocracy.foundation.integration.top.providers.MachineTOPProvider


class TOPIntegration {

    fun init() {
        TheOneProbe.theOneProbeImp.registerProvider(MachineTOPProvider())
    }

}