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
        var pipe_item: TextureAtlasSprite? = null
        var pipe_fluid: TextureAtlasSprite? = null
        var pipe_energy: TextureAtlasSprite? = null
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerTextureAtlas(event: TextureStitchEvent.Pre) {
        connector_energy = event.map.registerSprite(getIcon("extra/connector_energy"))
        connector_inventory = event.map.registerSprite(getIcon("extra/connector_inventory"))
        pipe_item = event.map.registerSprite(getIcon("block/steel"))
        pipe_fluid = event.map.registerSprite(getIcon("block/steel_dark"))
        pipe_energy = event.map.registerSprite(getIcon("extra/connector_inventory"))
    }

    fun getIcon(name: String): ResourceLocation {
        return ResourceLocation("technocracy.foundation", name)
    }
}