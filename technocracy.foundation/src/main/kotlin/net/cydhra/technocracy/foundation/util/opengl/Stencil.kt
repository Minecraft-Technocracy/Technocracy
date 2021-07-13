package net.cydhra.technocracy.foundation.util.opengl

import net.minecraft.client.shader.Framebuffer
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11


@SideOnly(Side.CLIENT)
class Stencil(val buffer: Framebuffer, body: Stencil.() -> Unit) {

    val valid: Boolean
    private var bit = 0
    private var mask = 0

    init {
        bit = MinecraftForgeClient.reserveStencilBit()

        val enabled = buffer.isStencilEnabled || buffer.enableStencil()

        if (bit >= 0) {
            if (!enabled) {
                MinecraftForgeClient.releaseStencilBit(bit)
            } else {
                mask = 1 shl bit
            }
        }

        valid = mask != 0

        body.invoke(this)

        destroy()
    }

    fun clear(value: Boolean) {
        GL11.glStencilMask(mask)
        GL11.glClearStencil(if (value) mask else 0)
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT)
    }

    fun op(fail: Int, zfail: Int = fail, zpass: Int = fail) {
        GL11.glStencilMask(mask)
        GL11.glStencilOp(fail, zfail, zpass)
    }

    fun func(func: Int, value: Boolean) {
        GL11.glStencilFunc(func, if (value) mask else 0, mask)
    }

    private fun destroy() {
        if (valid) {
            MinecraftForgeClient.releaseStencilBit(bit)
        }
    }
}