package net.cydhra.technocracy.foundation.tileentity.api

import net.cydhra.technocracy.foundation.client.gui.machine.Tab

/**
 * An interface for tile entities that offer a GUI
 */
interface TCTileEntityGuiProvider {

    /**
     * @return an array of tabs that are relevant for interaction with this tile entity.
     */
    fun getAvailableGUITabs(): Array<Tab>
}