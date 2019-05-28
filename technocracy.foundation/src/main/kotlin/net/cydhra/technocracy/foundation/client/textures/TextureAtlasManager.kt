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
        lateinit var connector_energy: TextureAtlasSprite
        lateinit var connector_inventory: TextureAtlasSprite
        lateinit var pipe_item: ResourceLocation
        lateinit var pipe_fluid: ResourceLocation
        lateinit var pipe_energy: ResourceLocation
        lateinit var pipe_node: ResourceLocation
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerTextureAtlas(event: TextureStitchEvent.Pre) {
        connector_energy = event.map.registerSprite(getIcon("extra/connector_energy"))
        connector_inventory = event.map.registerSprite(getIcon("extra/connector_inventory"))
        pipe_item = ResourceLocation("technocracy.foundation", "textures/block/steel.png")
        pipe_fluid = ResourceLocation("technocracy.foundation", "textures/block/steel_dark.png")
        pipe_energy = ResourceLocation("technocracy.foundation", "textures/block/boiler_wall.png")
        pipe_node = ResourceLocation("technocracy.foundation", "textures/block/frame_corners.png")
    }

    fun getIcon(name: String): ResourceLocation {
        return ResourceLocation("technocracy.foundation", name)
    }
}