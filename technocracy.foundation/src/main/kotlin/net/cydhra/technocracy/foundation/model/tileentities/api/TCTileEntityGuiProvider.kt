package net.cydhra.technocracy.foundation.model.tileentities.api

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * An interface for tile entities that offer a GUI
 */
interface TCTileEntityGuiProvider {

    /**
     * @return the gui to display to all players
     */
    @SideOnly(Side.CLIENT)
    fun getGui(player: EntityPlayer?): TCGui

    fun getContainer(player: EntityPlayer?): TCContainer
}