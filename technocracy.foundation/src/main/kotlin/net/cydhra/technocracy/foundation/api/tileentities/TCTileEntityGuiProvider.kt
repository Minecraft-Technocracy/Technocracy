package net.cydhra.technocracy.foundation.api.tileentities

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.entity.player.EntityPlayer

/**
 * An interface for tile entities that offer a GUI
 */
interface TCTileEntityGuiProvider {

    /**
     * @return the gui to display to all players
     */
    fun getGui(player: EntityPlayer?): TCGui {
        return getGui(player, null)
    }

    fun getGui(player: EntityPlayer?, gui: TCGui?): TCGui
}