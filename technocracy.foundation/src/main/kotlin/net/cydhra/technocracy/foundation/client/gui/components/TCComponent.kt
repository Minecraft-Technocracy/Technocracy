package net.cydhra.technocracy.foundation.client.gui.components

import kotlin.properties.Delegates

abstract class TCComponent : ITCComponent {
    var componentId by Delegates.notNull<Int>()
}

interface ITCComponent {

    fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float)
    fun drawTooltip(mouseX: Int, mouseY: Int)

    fun update()

    fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int)
    fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean

}
