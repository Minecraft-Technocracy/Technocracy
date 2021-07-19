package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractTileEntityDirectionalCapabilityComponent


interface ICompositeComponent : ITCComponent {
    fun drawBackground(x: Int, y: Int)
    fun drawChildren(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float)

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(x, y)
        drawChildren(x, y, mouseX, mouseY, partialTicks)
    }

    fun getElements(): List<Pair<ITCComponent, AbstractTileEntityDirectionalCapabilityComponent>>
}