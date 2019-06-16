package net.cydhra.technocracy.foundation.client.textures

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
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
        lateinit var pipe_item: TextureAtlasSprite
        lateinit var pipe_fluid: TextureAtlasSprite
        lateinit var pipe_energy: TextureAtlasSprite
        lateinit var pipe_node: TextureAtlasSprite

        private lateinit var textureMap: TextureMap

        /**
         * Returns a texture atlas sprite for a resource location of the texture map
         */
        fun getTextureAtlasSprite(location: ResourceLocation): TextureAtlasSprite {
            return textureMap.getTextureExtry(location.toString()) ?: textureMap.missingSprite
        }

        /**
         * Returns a resource location mapped to the technocracy resource domain
         */
        fun getResourceLocation(name: String): ResourceLocation {
            return ResourceLocation("technocracy.foundation", name)
        }

        /**
         * Returns the texture for connections of a specific pipe type
         */
        fun getTextureForConnectionType(type: PipeType): TextureAtlasSprite {
            return when (type) {
                PipeType.ENERGY -> TextureAtlasManager.pipe_energy
                PipeType.FLUID -> TextureAtlasManager.pipe_fluid
                PipeType.ITEM -> TextureAtlasManager.pipe_item
            }
        }
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerTextureAtlas(event: TextureStitchEvent.Pre) {
        textureMap = event.map
        connector_energy = event.map.registerSprite(getResourceLocation("extra/connector_energy"))
        connector_inventory = event.map.registerSprite(getResourceLocation("extra/connector_inventory"))
        pipe_item = event.map.registerSprite(getResourceLocation("block/steel"))
        pipe_fluid = event.map.registerSprite(getResourceLocation("block/steel_dark"))
        pipe_energy = event.map.registerSprite(getResourceLocation("block/boiler_wall"))
        pipe_node = event.map.registerSprite(getResourceLocation("block/frame_corners"))
    }
}