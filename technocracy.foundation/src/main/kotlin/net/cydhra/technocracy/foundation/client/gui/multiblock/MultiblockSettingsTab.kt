package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.ResourceLocation

class MultiblockSettingsTab(parent: TCGui, val controller: TileEntityMultiBlockPart<*>) : TCTab("Settings", parent, icon = ResourceLocation("technocracy.foundation", "textures/item/gear.png")) {

    override fun init() {}

}