package net.cydhra.technocracy.foundation.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.blocks.api.MultiBlockBaseDelegate
import net.cydhra.technocracy.foundation.blocks.api.TCMultiBlock
import net.cydhra.technocracy.foundation.tileentity.api.TCControllerTileEntity
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity

/**
 * A plain, non-rotatable block that creates a multiblock tile entity when placed, using the given constructor
 *
 * @param unlocalizedName registry and language name of the block
 * @param tileEntityConstructor constructor for the tile entity that is created
 */
class PlainMultiBlockPartBlock<T>(unlocalizedName: String, tileEntityConstructor: () -> T)
    : AbstractTileEntityBlock(unlocalizedName, material = Material.IRON),
        TCMultiBlock<T> by MultiBlockBaseDelegate<T>(tileEntityConstructor)
        where T : TileEntity, T : TCControllerTileEntity, T : IMultiblockPart