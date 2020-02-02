package net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.multiblock.BaseMultiblockTab
import net.cydhra.technocracy.foundation.content.multiblock.SalineMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import org.lwjgl.util.Color

class TileEntitySalineController :
        TileEntityMultiBlockPart<SalineMultiBlock>(SalineMultiBlock::class, ::SalineMultiBlock),
        ITileEntityMultiblockController {

}