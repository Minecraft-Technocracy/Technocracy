package net.cydhra.technocracy.foundation.util

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Predicate
import kotlin.math.pow


@SideOnly(Side.CLIENT)
object ColorUtil : ISelectiveResourceReloadListener {

    private val colorMap = mutableMapOf<Fluid, Int>()

    init {
        (Minecraft.getMinecraft().resourceManager as SimpleReloadableResourceManager).registerReloadListener(this)
    }

    @SideOnly(Side.CLIENT)
    fun getColor(fluidStack: FluidStack?): Int {
        val fluid = fluidStack?.fluid ?: return -1
        return colorMap.getOrPut(fluid) {
            val sprite = TextureAtlasManager.getTextureAtlasSprite(fluid.still)

            var totalr = 0.0
            var totalg = 0.0
            var totalb = 0.0
            var count = 0

            for (frames in 0 until sprite.frameCount) {
                for (row in sprite.getFrameTextureData(frames)) {
                    for (column in row) {
                        val a = column shr 24 and 0xFF
                        //limit alpha
                        if (a <= 20)
                            continue
                        count++
                        val r = (column shr 16 and 0xFF).toDouble()
                        val g = (column shr 8 and 0xFF).toDouble()
                        val b = (column and 0xFF).toDouble()
                        totalr += r.pow(2.2)
                        totalg += g.pow(2.2)
                        totalb += b.pow(2.2)
                    }
                }
            }

            if (count == 0)
                return@getOrPut fluid.color

            totalr /= count.toDouble()
            totalg /= count.toDouble()
            totalb /= count.toDouble()

            val tintr = (fluid.color shr 16 and 0xFF).toDouble() / 255.0
            val tintg = (fluid.color shr 8 and 0xFF).toDouble()/ 255.0
            val tintb = (fluid.color and 0xFF).toDouble()/ 255.0

            totalr = totalr.pow(1 / 2.2) * tintr
            totalg = totalg.pow(1 / 2.2) * tintg
            totalb = totalb.pow(1 / 2.2) * tintb

            0xFF shl 24 or (totalr.toInt() shl 16) or (totalg.toInt() shl 8) or totalb.toInt()
        }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate<IResourceType>) {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
            colorMap.clear()
        }
    }
}