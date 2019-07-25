package net.cydhra.technocracy.foundation.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.cydhra.technocracy.foundation.integration.top.providers.MachineTOPProvider
import net.cydhra.technocracy.foundation.integration.top.providers.MultiBlockTOPProvider


class TOPIntegration {

    fun init() {
        TheOneProbe.theOneProbeImp.registerProvider(MachineTOPProvider())
    }

}