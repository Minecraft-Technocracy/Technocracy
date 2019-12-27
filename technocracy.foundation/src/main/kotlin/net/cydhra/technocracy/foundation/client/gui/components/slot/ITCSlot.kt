package net.cydhra.technocracy.foundation.client.gui.components.slot

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent

/**
 * Common interface to GUI item slots. Since two different kinds of slots are required, because player-inventories
 * and capability-inventories are not compatible, common behaviour is collected here.
 */
interface ITCSlot : TCComponent {

    fun setEnabled(enabled: Boolean)

    fun isEnabled(): Boolean
}