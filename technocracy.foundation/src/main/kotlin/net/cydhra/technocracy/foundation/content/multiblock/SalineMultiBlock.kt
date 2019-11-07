package net.cydhra.technocracy.foundation.content.multiblock

import net.cydhra.technocracy.foundation.content.blocks.*
import net.cydhra.technocracy.foundation.model.multiblock.api.TiledBaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.function.Predicate

class SalineMultiBlock(world: World) : TiledBaseMultiBlock(
        frameBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineFluidInput || it.block == salineControllerBlock ||
                    it.block == salineHeatingAgentInput || it.block == salineHeatingAgentOutput
        },
        sideBlockWhitelist = null,
        topBlockWhitelist = Predicate {
            it.block == Blocks.AIR
        },
        bottomBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineHeatedWallBlock || it.block == salineFluidOutput
        },
        interiorBlockWhitelist = null,
        tileFrameBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineFluidInput
        },
        tileSideBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineFluidInput || it.block == salineFluidOutput
        },
        tileSizeX = 5,
        tileSizeZ = 5,
        sizeY = 2,
        world = world
) {
    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        return mutableListOf()
    }

    override fun updateServer(): Boolean {
        return false
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 41
    }

    override fun updateClient() {
    }

}