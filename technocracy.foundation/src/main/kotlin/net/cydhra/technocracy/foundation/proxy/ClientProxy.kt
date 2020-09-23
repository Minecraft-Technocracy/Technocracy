package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.events.KeyEventHandler
import net.cydhra.technocracy.foundation.client.model.facade.FacadeItemModel
import net.cydhra.technocracy.foundation.client.model.pipe.PipeItemModel
import net.cydhra.technocracy.foundation.client.model.pipe.PipeModel
import net.cydhra.technocracy.foundation.client.model.tank.MutliBlockTankFluidModel
import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.content.blocks.pipe
import net.cydhra.technocracy.foundation.content.blocks.tankGlassBlock
import net.cydhra.technocracy.foundation.content.blocks.tankIOBlock
import net.cydhra.technocracy.foundation.content.blocks.tankWallBlock
import net.cydhra.technocracy.foundation.content.commands.ClearParticlesCommand
import net.cydhra.technocracy.foundation.content.commands.ClearTCCaches
import net.cydhra.technocracy.foundation.content.commands.ReloadShaderCommand
import net.cydhra.technocracy.foundation.content.items.facadeItem
import net.cydhra.technocracy.foundation.content.items.pipeItem
import net.cydhra.technocracy.foundation.content.items.structureMarkerItem
import net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard


/**
 * Client side implementation of sided proxy. Calls Common proxy and adds client-side-only behaviour like rendering
 * and animations.
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
class ClientProxy : CommonProxy() {

    companion object {
        val itemUpgradeGui = KeyBinding("key.item.upgradegui", Keyboard.KEY_H, "key.technocracy.category")
    }

    /**
     * Loads an [IAnimationStateMachine] from a model file at [location] on client side.
     */
    override fun loadAnimationStateMachine(location: ResourceLocation, parameters: ImmutableMap<String, ITimeValue>):
            IAnimationStateMachine? {
        return ModelLoaderRegistry.loadASM(location, parameters)
    }

    override fun preInit() {
        super.preInit()

        blockManager.initClient()
        itemManager.initClient()
        tileEntityManager.initClient()

        blockManager.linkBlockToModel(tankWallBlock, MutliBlockTankFluidModel())
        blockManager.linkBlockToModel(tankIOBlock, MutliBlockTankFluidModel())
        blockManager.linkBlockToModel(tankGlassBlock, MutliBlockTankFluidModel())
        blockManager.linkBlockToModel(pipe, PipeModel())

        itemManager.linkItemToModel(pipeItem, PipeItemModel())
        itemManager.linkItemToModel(facadeItem, FacadeItemModel())

        //Dev tools
        itemManager.prepareItemForRegistration(structureMarkerItem)
        MinecraftForge.EVENT_BUS.register(structureMarkerItem)

        TextureAtlasManager()
        MinecraftForge.EVENT_BUS.register(RenderEventListener())
        MinecraftForge.EVENT_BUS.register(KeyEventHandler)
    }

    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
        tileEntityManager.onClientInitialize()

        ClientRegistry.registerKeyBinding(itemUpgradeGui)

        ClientCommandHandler.instance.registerCommand(ClearParticlesCommand())
        ClientCommandHandler.instance.registerCommand(ReloadShaderCommand())
        ClientCommandHandler.instance.registerCommand(ClearTCCaches())
    }

    override fun postInit() {
        super.postInit()

        MinecraftForge.EVENT_BUS.register(TCParticleManager)
    }
}