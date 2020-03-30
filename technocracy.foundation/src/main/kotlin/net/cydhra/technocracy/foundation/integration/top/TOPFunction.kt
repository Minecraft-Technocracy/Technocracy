package net.cydhra.technocracy.foundation.integration.top

import mcjty.theoneprobe.api.ITheOneProbe
import net.cydhra.technocracy.foundation.integration.top.providers.MachineTOPProvider

/**
 * A function implementation that is sent via inter-mod-comm to TOP, and is subsequently supplied with an
 * [ITheOneProbe] instance that can be used to register out TOP provider at TOP.
 */
@Suppress("unused") // used via reflection
class TOPFunction : java.util.function.Function<ITheOneProbe, Void?> {
    override fun apply(mod: ITheOneProbe): Void? {
        mod.registerProvider(MachineTOPProvider())
        return null
    }
}