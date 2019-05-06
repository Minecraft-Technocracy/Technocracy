package net.cydhra.technocracy.foundation.client.textures

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class TextureAtlasManager {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    companion object {
        var connector_energy: TextureAtlasSprite? = null
        var connector_inventory: TextureAtlasSprite? = null
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerTextureAltas(event: TextureStitchEvent.Pre) {
        connector_energy = event.map.registerSprite(getIcon("extra/connector_energy"))
        connector_inventory = event.map.registerSprite(getIcon("extra/connector_inventory"))
    }

    fun getIcon(name: String): ResourceLocation {
        return ResourceLocation("technocracy.foundation", name)
    }
}