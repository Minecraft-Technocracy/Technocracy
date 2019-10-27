package net.cydhra.technocracy.foundation.content.multiblock

import net.cydhra.technocracy.foundation.content.blocks.*
import net.cydhra.technocracy.foundation.model.multiblock.api.TiledBaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.minecraft.world.World
import java.util.function.Predicate

class SalineMultiBlock(world: World) : TiledBaseMultiBlock(
        frameBlockWhitelist = Predicate { it.block == salineWallBlock },
        sideBlockWhitelist = Predicate {
            it.block == capacitorWallBlock || it.block == capacitorControllerBlock
        },
        topBlockWhitelist = Predicate {
            it.block == capacitorConnectorBlock || it.block == capacitorWallBlock || it.block ==
                    capacitorEnergyPortBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == capacitorWallBlock
        },
        interiorBlockWhitelist = Predicate {
            it.block == sulfuricAcidBlock || it.block == leadBlock || it.block ==
                    leadOxideBlock
        },
        tileFrameBlockWhitelist = Predicate { it.block == leadBlock },
        tileSideBlockWhitelist = Predicate { it.block == leadBlock },
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