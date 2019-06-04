package net.cydhra.technocracy.foundation.client.gui.components

//import net.cydhra.technocracy.foundation.util.opengl.Rect
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class AbstractProgressbar(val xPosition: Int, val yPosition: Int) : TCComponent {

    // 0 = min, 1 = max
    var progress: Float = 0F;

    val width: Int = 21;
    val height: Int = 15;

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)

        GlStateManager.color(1F, 1F, 1F, 1F)

//        Rect.drawModalRectWithCustomSizedTexture(xPosition - 1, yPosition - 1, 0F, 27F, width + 1F, height + 1F, 256F,
//                256F)
//        Rect.drawModalRectWithCustomSizedTexture(xPosition - 1, yPosition - 1, 0F, 27F + height, (width * progress) + 1F, height + 1F, 256F,
//                256F)


    }

    override fun update() {

    }

}