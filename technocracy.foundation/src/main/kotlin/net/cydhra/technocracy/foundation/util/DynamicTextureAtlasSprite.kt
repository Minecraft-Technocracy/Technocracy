package net.cydhra.technocracy.foundation.util

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation


class DynamicTextureAtlasSprite(spriteName: String) : TextureAtlasSprite(spriteName) {
    companion object {
        fun makeAtlasSprite(spriteResourceLocation: ResourceLocation): DynamicTextureAtlasSprite {
            return DynamicTextureAtlasSprite(spriteResourceLocation.toString())
        }
    }

    var allowNextUpdate = false

    fun resetAnimation() {
        frameCounter = this.frameCount - 1
        tickCounter = 0
        allowNextUpdate = true
    }

    fun increaseAnimation() {
        allowNextUpdate = true
    }

    fun setAnimationTime(time: Int) {
        //set the frame counter to one before the one we actually want
        val needUpdate = frameCounter != time
        if (needUpdate) {
            val tmp = time - 1
            frameCounter = if (tmp < 0) this.frameCount - 1 else tmp
            allowNextUpdate = true
        }
    }

    override fun updateAnimation() {
        if (allowNextUpdate) {
            allowNextUpdate = false
            super.updateAnimation()
        }
    }
}