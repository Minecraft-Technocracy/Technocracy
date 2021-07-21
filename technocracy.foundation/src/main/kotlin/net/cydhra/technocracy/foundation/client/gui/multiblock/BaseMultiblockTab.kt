package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPart

abstract class BaseMultiblockTab(val controller: TileEntityMultiBlockPart<*>, parent: TCGui, icon: TCIcon) : TCTab(name = controller.blockType.localizedName, parent = parent, icon = icon) {

}