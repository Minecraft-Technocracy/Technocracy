package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.client.model.CustomModelLoader
import net.cydhra.technocracy.foundation.client.model.MachineConnectorModel
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine

/**
 * Client side implementation of sided proxy. Calls Common proxy and adds client-side-only behaviour like rendering
 * and animations.
 */
class ClientProxy : CommonProxy() {

    override fun init() {
        super.init()
        ItemManager.registerItemColors()
        BlockManager.registerBlockColors()
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
        val map = hashMapOf<String, IModel>()
        map.put("models/block/electric_furnace", MachineConnectorModel())
        val loader = CustomModelLoader(map)
        ModelLoaderRegistry.registerLoader(loader)
    }
}