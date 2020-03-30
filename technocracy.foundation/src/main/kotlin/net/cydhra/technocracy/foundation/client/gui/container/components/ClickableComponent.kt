package net.cydhra.technocracy.foundation.client.gui.container.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity


class ClickableComponent(val componentId: Int, val onClick: (player: EntityPlayer, tileEntity: TileEntity?, button: Int) -> Unit) : IContainerComponent {

}