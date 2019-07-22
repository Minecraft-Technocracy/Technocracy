package net.cydhra.technocracy.foundation.client.gui.components

interface TCComponent {

    fun draw(mouseX: Int, mouseY: Int, partialTicks: Float)
    fun drawTooltip(mouseX: Int, mouseY: Int)

    fun update()

    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int)
    fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean

}
