package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

class MultiblockSettingsTab(parent: TCGui, val controller: TileEntityMultiBlockPart<*>, val player: EntityPlayer) : TCTab("Settings", parent, icon = ResourceLocation("technocracy.foundation",
        "textures/item/gear.png")) {

    override fun init() {

    }

}