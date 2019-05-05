package net.cydhra.technocracy.foundation.client.gui.components

interface TCComponent {

    fun draw(mouseX: Int, mouseY: Int, partialTicks: Float)

    fun update()

}
