package net.cydhra.technocracy.foundation.liquids.general

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.BaseLiquidBlock
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.minecraft.block.material.Material
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object FluidManager {


    /**
     * A list that saves all fluids to be registered
     */
    private val fluidsToRegister = mutableListOf<BaseFluidPlaceable>()

    /**
     * Register a fluid at the FluidRegistry. Since fluids must be registered before their respective fluid blocks,
     * they are not prepared and registered alongside blocks in the block event, but directly in pre-init using this
     * method
     */

    fun registerFluid(fluid: Fluid) {
        FluidRegistry.registerFluid(fluid)
        FluidRegistry.addBucketForFluid(fluid)

        if(fluid is BaseFluidPlaceable) {
            BlockManager.prepareBlocksForRegistration(BaseLiquidBlock(fluid, fluid.name, Material.WATER))
        }
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterRenders(@Suppress("UNUSED_PARAMETER") event: ModelRegistryEvent) {
    }
}