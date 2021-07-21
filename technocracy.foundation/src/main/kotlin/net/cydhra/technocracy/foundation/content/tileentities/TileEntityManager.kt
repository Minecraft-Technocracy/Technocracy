package net.cydhra.technocracy.foundation.content.tileentities

import net.minecraft.block.Block
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.reflect.KClass

/**
 * Utility class that handles registration of tile entities and their subsequent dependencies like renderers.
 */
class TileEntityManager(val modName: String) {

    /**
     * A list of all prepared tile entities that are registered during block registration phase
     */
    private val preparedTileEntities = mutableListOf<KClass<out TileEntity>>()

    @SideOnly(Side.CLIENT)
    private lateinit var associatedSpecialRenderers: MutableMap<KClass<out TileEntity>, TileEntitySpecialRenderer<*>>

    @SideOnly(Side.CLIENT)
    fun initClient() {
        associatedSpecialRenderers = mutableMapOf()
    }

    fun <T : TileEntity> prepareTileEntityForRegistration(tileEntityClass: KClass<out T>) {
        this.preparedTileEntities += tileEntityClass
    }

    @SideOnly(Side.CLIENT)
    fun <T : TileEntity> linkTileEntityWithRenderer(tileEntityClass: KClass<out T>,
                                                    specialRenderer: TileEntitySpecialRenderer<T>) {
        this.associatedSpecialRenderers[tileEntityClass] = specialRenderer
    }

    /**
     * Register the prepared tile entities during block registration. Tile entities do not have their own
     * registration event, yet. Documentation states they shall use the block event instead.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(@Suppress("UNUSED_PARAMETER") event: RegistryEvent.Register<Block>) {
        preparedTileEntities.forEach { tileEntityClass ->
            GameRegistry.registerTileEntity(tileEntityClass.java,
                    ResourceLocation("$modName:${tileEntityClass.simpleName}"))
        }
    }

    /**
     * Called upon init phase by the client proxy. Initializes renderers.
     */
    @SideOnly(Side.CLIENT)
    fun onClientInitialize() {
        associatedSpecialRenderers.forEach { tileEntityClass, specialRenderer ->
            @Suppress("UNCHECKED_CAST") // this class'es contract ensures that it works
            ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass.java,
                    specialRenderer as TileEntitySpecialRenderer<in TileEntity>)
        }
    }

}